package org.nem.chat.client.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
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
import org.nem.chat.protocol.model.Header;
import org.nem.chat.protocol.model.Message;
import org.nem.chat.protocol.model.User;
import org.nem.chat.protocol.model.UserList;

/**
 *
 * @author nemanja.marjanovic
 */
public class Chat {

    private static final Logger LOG = Logger.getLogger("CLIENT");

    private final User identity;
    private String privateKey;
    private final Map<Long, User> buddies = new ConcurrentHashMap<>();
    private final Map<Long, Session> sessions = new ConcurrentHashMap<>();
    private final Map<Long, Session> openSessionsRequests = new ConcurrentHashMap<>();
    private final Map<Long, Session> closeSessionsRequests = new ConcurrentHashMap<>();
    private final Queue<Session> openedSessions = new ConcurrentLinkedQueue<>();
    private final Queue<Session> closedSessions = new ConcurrentLinkedQueue<>();
    private final PrintWriter out;
    private final BufferedReader in;
    private final MessageComposer messageComposer = new MessageComposer();

    public Chat() {
        try {
            Socket socket = new Socket("localhost", 9011);
            this.out = new PrintWriter(socket.getOutputStream(), true);
            this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        } catch (Exception e) {
            LOG.severe(e.getMessage());
            throw new RuntimeException();
        }

        this.identity = new User();
        this.identity.setPublicKey("public key");
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
        this.sendMessage(message);
    }

    public void logout() {
        if (isLogged()) {
            Message message = this.messageComposer.logoutMessage(
                    this.identity.getId());
            this.sendMessage(message);
            this.identity.setId(null);
        }
    }

    public List<User> getAvailableBuddies() {
        this.sendMessage(messageComposer.usersMessage());
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
        this.sendMessage(message);
        this.openSessionsRequests.put(newSession.getId(), newSession);
    }

    public void closeChatSession(final Long id) {
        if (this.sessions.containsKey(id)) {
            Session sessionToClose = this.sessions.get(id);
            Message message = this.messageComposer.sendCloseSessionMessage(
                    sessionToClose, this.identity.getId());
            this.sendMessage(message);
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
            this.sendMessage(message);
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

    private void sendMessage(final Message message) {

        if (message.getType().equals("Server")) {
            //sign header with server public key
        } else {
              //sign header with client public key
        }
        
        //hash message
        //sign hash
        //return string

        this.out.println(new String(Message.BYTER.toBytes(message), StandardCharsets.UTF_8));
    }

    private void listenForMessages() {
        String fromServer;
        try {
            while ((fromServer = in.readLine()) != null) {
                this.processIncomingMessage(
                        Message.BYTER.fromBytes(fromServer.getBytes(StandardCharsets.UTF_8)));
            }
        } catch (IOException | ClassNotFoundException ex) {
            LOG.severe(ex.getMessage());
        }
    }

    private void processIncomingMessage(final Message receivedMessage)
            throws IOException, ClassNotFoundException {

        Header receivedHeader = Header.BYTER.fromBytes(receivedMessage.getHeader());
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
                this.sendMessage(response);
                break;

            case "session-open-confirm":
                this.openedSessions.add(
                        this.openSessionsRequests.remove(receivedHeader.getSessionid()));
                break;

            case "session-close":
                this.closedSessions.add(
                        this.sessions.get(
                                receivedHeader.getSessionid()));
                response = messageComposer.sendCloseSessionConfirmMessage(
                        receivedHeader.getSessionid(), this.identity.getId(), receivedHeader.getFrom());
                this.sendMessage(response);
                break;

            case "session-close-confirm":
                this.closedSessions.add(
                        this.closeSessionsRequests.remove(receivedHeader.getSessionid()));
                break;

            default:
                break;
        }
    }
}
