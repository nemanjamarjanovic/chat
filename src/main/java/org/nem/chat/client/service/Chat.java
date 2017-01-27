package org.nem.chat.client.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.security.PublicKey;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executors;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import org.nem.chat.client.model.Session;
import org.nem.chat.protocol.model.AsymetricKey;
import org.nem.chat.protocol.model.Header;
import org.nem.chat.protocol.model.Message;
import org.nem.chat.protocol.model.MessageBuilder;
import org.nem.chat.protocol.model.User;
import org.nem.chat.protocol.model.UserList;
import org.nem.chat.protocol.service.Envelope;
import org.nem.chat.protocol.service.HashedMessage;

/**
 *
 * @author nemanja.marjanovic
 */
public class Chat {

    private static final Logger LOG = Logger.getLogger("CLIENT");

    private final User identity;
    private final AsymetricKey key;
    private final Map<Long, User> buddies = new ConcurrentHashMap<>();
    private final Map<Long, Session> sessions = new ConcurrentHashMap<>();
    private final Map<Long, Session> openSessionsRequests = new ConcurrentHashMap<>();
    private final Map<Long, Session> closeSessionsRequests = new ConcurrentHashMap<>();
    private final Queue<Session> openedSessions = new ConcurrentLinkedQueue<>();
    private final Queue<Session> closedSessions = new ConcurrentLinkedQueue<>();
    private final PrintWriter out;
    private final BufferedReader in;
    private final MessageComposer messageComposer = new MessageComposer();
    private PublicKey serverPublicKey;

    public Chat() {
        try {
            Socket socket = new Socket("localhost", 9011);
            this.out = new PrintWriter(socket.getOutputStream(), true);
            this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.key = new AsymetricKey();
        } catch (Exception exception) {
            throw new RuntimeException(exception.getMessage());
        }

        this.identity = new User();
        this.identity.setPublicKey(this.key.getPublicKey());
        Executors.newSingleThreadExecutor().execute(this::listenForMessages);
    }

    public boolean isLogged() {
        return this.identity.getId() != null;
    }

    public String title() {
        String title = "Not logged in";
        if (isLogged()) {
            title = this.identity.getName() + " - " + this.identity.getId();
        }
        return title;
    }

    public void login(final String name) {
        this.identity.setName(name);
        Message message = this.messageComposer.loginMessage(
                this.identity.getName(),
                this.identity.getPublicKey());
        this.sendMessage(message, serverPublicKey);
    }

    public void logout() {
        if (isLogged()) {
            Message message = this.messageComposer.logoutMessage(
                    this.identity.getId());
            this.sendMessage(message, serverPublicKey);
            this.identity.setId(null);
        }
    }

    public List<User> getAvailableBuddies() {
        this.sendMessage(messageComposer.usersMessage(), serverPublicKey);
        return this.buddies.keySet()
                .stream()
                .filter(k -> !k.equals(this.identity.getId()))
                .map(k -> this.buddies.get(k))
                .collect(Collectors.toList());
    }

    public void openChatSession(final User buddy) {
        Session newSession = new Session(buddy);
        Message message = this.messageComposer.sendOpenSessionMessage(newSession,
                this.identity.getId());
        this.sendMessage(message, buddy.getPublicKey());
        this.openSessionsRequests.put(newSession.getId(), newSession);
    }

    public void closeChatSession(final Long id) {
        if (this.sessions.containsKey(id)) {
            Session sessionToClose = this.sessions.get(id);
            Message message = this.messageComposer.sendCloseSessionMessage(
                    sessionToClose, this.identity.getId());
            this.sendMessage(message, sessionToClose.getBuddy().getPublicKey());
            this.closeSessionsRequests.put(sessionToClose.getId(), sessionToClose);
        }
    }

    public Session getOpenedSession() {
        Session opened = this.openedSessions.poll();
        if (opened != null) {
            this.sessions.put(opened.getId(), opened);
        }
        return opened;
    }

    public Session getClosedSession() {
        Session closed = this.closedSessions.poll();
        if (closed != null) {
            this.sessions.remove(closed.getId());
        }
        return closed;
    }

    public void sendChatMessage(final Long session, final String text) {
        Session chatSession = this.sessions.get(session);
        if (chatSession != null) {
            Message message = messageComposer.sendChatMessage(chatSession,
                    this.identity.getId(), chatSession.encrypt(text));
            this.sendMessage(message, chatSession.getBuddy().getPublicKey());
        }
    }

    public String receiveChatMessage(final Long session) {
        String message = null;
        Session chatSession = this.sessions.get(session);
        if (chatSession != null) {
            Optional<String> incoming = chatSession.getMessage();
            if (incoming.isPresent()) {
                message = incoming.get();
            }
        }
        return message;
    }

    private void sendMessage(final Message message, final Key key) {

        Envelope serverHeaderEnvelope = new Envelope(message.getServerHeader());
        byte[] packedServerHeader = serverHeaderEnvelope.pack(null);//server

        Envelope headerEnvelope = new Envelope(message.getHeader());
        byte[] packedHeader = headerEnvelope.pack(key);

        Message packedMessage = MessageBuilder.builder().serverHeader(packedServerHeader)
                .header(packedHeader).body(message.getBody()).build();

        HashedMessage hashedMessage = new HashedMessage(packedMessage);
        byte[] signature = hashedMessage.signature(this.key.getPrivateKey());

        String finalMessage = new String(signature, StandardCharsets.UTF_8) + ":s"
                + new String(Message.BYTER.toBytes(packedMessage), StandardCharsets.UTF_8);

        this.out.println(finalMessage);
    }

    private void listenForMessages() {
        String fromServer;
        try {
            while ((fromServer = in.readLine()) != null) {

                String split[] = fromServer.split(":");
                Message original = Message.BYTER.fromBytes(split[1].getBytes(StandardCharsets.UTF_8));

                Envelope headerEnvelope = new Envelope(original.getHeader());
                Header unpackedHeader = Header.BYTER.fromBytes(headerEnvelope.unpack(this.key.getPrivateKey()));
                PublicKey fromKey = this.buddies.get(unpackedHeader.getFrom()).getPublicKey();

                HashedMessage hashedMessage = new HashedMessage(original);
                hashedMessage.verifySignature(fromKey, split[0].getBytes(StandardCharsets.UTF_8));

                this.processIncomingMessage(original, unpackedHeader);
            }
        } catch (IOException | ClassNotFoundException ex) {
            LOG.severe(ex.getMessage());
        }
    }

    private void processIncomingMessage(final Message receivedMessage, final Header receivedHeader)
            throws IOException, ClassNotFoundException {

        //Header receivedHeader = Header.BYTER.fromBytes(receivedMessage.getHeader());
        LOG.info(receivedHeader.toString());
        Message response;
        switch (receivedHeader.getAction()) {

            case "chat":
                Session chatSession = this.sessions.get(receivedHeader.getSessionid());
                chatSession.addMessage(chatSession.decrypt(receivedMessage.getBody()));
                break;

            case "login":
                identity.setId(receivedHeader.getUserid());
                break;

            case "users":
                this.buddies.clear();
                UserList userList = UserList.BYTER.fromBytes(receivedMessage.getBody());
                userList.getList().forEach(user -> {
                    this.buddies.put(user.getId(), user);
                });
                break;

            case "session-open":
                Session newSession = new Session(receivedHeader.getSessionid(), this.buddies.get(receivedHeader.getFrom()),
                        receivedHeader.getSessionKey(), receivedHeader.getSymetricAlg());
                this.openedSessions.add(newSession);
                response = messageComposer.sendOpenSessionConfirmMessage(
                        newSession.getId(), this.identity.getId(), receivedHeader.getFrom());
                this.sendMessage(response, newSession.getBuddy().getPublicKey());
                break;

            case "session-open-confirm":
                this.openedSessions.add(
                        this.openSessionsRequests.remove(receivedHeader.getSessionid()));
                break;

            case "session-close":
                Session closedSession = this.sessions.get(receivedHeader.getSessionid());
                this.closedSessions.add(closedSession);
                response = messageComposer.sendCloseSessionConfirmMessage(
                        receivedHeader.getSessionid(), this.identity.getId(), receivedHeader.getFrom());
                this.sendMessage(response, closedSession.getBuddy().getPublicKey());
                break;

            case "session-close-confirm":
                this.closedSessions.add(
                        this.closeSessionsRequests.remove(receivedHeader.getSessionid()));
                break;

            default:
                break;
        }
    }

    public int sessionCount() {
        return this.sessions.size();
    }
}
