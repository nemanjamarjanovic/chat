package org.nem.chat.client.service;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import org.nem.chat.transport.MessageStream;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import org.nem.chat.client.model.ChatMessage;
import org.nem.chat.client.model.Session;
import org.nem.chat.crypto.model.AsymetricKey;
import org.nem.chat.crypto.model.SymetricKey;
import org.nem.chat.crypto.service.Envelope;
import org.nem.chat.protocol.model.ByteSerializer;
import org.nem.chat.protocol.model.Message;
import org.nem.chat.protocol.model.User;
import org.nem.chat.protocol.model.UserList;

/**
 *
 * @author nemanja.marjanovic
 */
public class Chat {

    private static final Logger LOG = Logger.getLogger("CLIENT");

    private ServerStream serverStream;
    private MessageStream serverMessageStream;
    private MessageStream messageStream;
    private ExecutorService listener;
    private final User identity = new User();
    private final AsymetricKey key = new AsymetricKey();
    private final Map<Long, Session> sessions = new ConcurrentHashMap<>();
    private final Map<Long, User> buddies = new ConcurrentHashMap<>();
    private final Queue<Session> openedSessions = new ConcurrentLinkedQueue<>();
    private final Queue<Session> closedSessions = new ConcurrentLinkedQueue<>();
    private final ByteSerializer<ChatMessage> chatMessageByter = new ByteSerializer<>();
    private final ByteSerializer<UserList> userByter = new ByteSerializer<>();

    public void login(final String name) {

        this.serverStream = new ServerStream(key, name);
        this.identity.setPublicKey(this.key.getPublicKey());
        this.identity.setId(this.serverStream.getId());
        this.identity.setName(name);
        this.serverMessageStream = new MessageStream(serverStream, this.identity,
                new User(null, null, this.serverStream.getServerPublicKey()));
        this.messageStream = new MessageStream(serverStream);
        this.listener = Executors.newSingleThreadExecutor();
        this.listener.execute(this::listen);
    }

    public void logout() {
        if (isLogged()) {
            this.identity.setId(null);
            this.serverMessageStream.writeString("End");
        }
    }

    public List<User> getAvailableBuddies() {
        this.serverMessageStream.writeString("Users");

        this.sessions.values().stream()
                .filter(s -> !this.buddies.containsKey(s.getBuddy().getId()))
                .forEach(s -> this.closedSessions.add(s));

        return this.buddies.keySet().stream().filter(k -> !k.equals(this.identity.getId()))
                .map(k -> this.buddies.get(k)).collect(Collectors.toList());
    }

    public void openChatSession(final User buddy) {
        Session session = new Session(buddy);
        String sessionData = session.getProcess().getSymetricKey().getSymetric()
                + ":" + session.getProcess().getSymetricKey().getSessionKey()
                + ":" + session.getId().toString();
        byte[] keys = sessionData.getBytes(StandardCharsets.UTF_8);
        byte[] packed = Envelope.fromByte(keys).pack(buddy.getPublicKey());
        MessageStream ms = new MessageStream(serverStream, this.identity, buddy, "Open");
        ms.writeBytes(packed);
        this.openedSessions.add(session);
        LOG.info("NEW SESSION: " + session.getId() + " ALG:" + session.getProcess().getSymetricKey().getSymetric());
    }

    public void sendChatMessage(final Long session, final String text) {
        Session chatSession = this.sessions.get(session);
        if (chatSession != null) {
            byte[] encrypted = chatSession.getProcess().encrypt(text);
            ChatMessage chatMessage = new ChatMessage(session, encrypted);
            MessageStream ms = new MessageStream(serverStream, this.identity,
                    chatSession.getBuddy(), "Message");
            ms.writeBytes(chatMessageByter.toBytes(chatMessage));
            this.checkEndMessage(text, chatSession);
        }
    }

    public void listen() {
        while (true) {
            Message message = this.messageStream.readMessage();
            //LOG.info("New Message(" + this.identity.getId() + "): " + message.toString());

            switch (message.getCommand()) {

                case "Users":
                    UserList fromBytes = userByter.fromBytes(message.getBody());
                    this.buddies.clear();
                    fromBytes.getList().stream().forEach(u -> {
                        this.buddies.put(u.getId(), u);
                    });
                    break;

                case "Open":
                    byte[] packed = message.getBody();
                    byte[] unpacked = Envelope.fromByte(packed).unpack(this.key.getPrivateKey());
                    String sessionData = new String(unpacked, StandardCharsets.UTF_8);
                    String split[] = sessionData.split(":");
                    Long sessionId = Long.parseLong(split[2]);
                    SymetricKey sk = new SymetricKey(split[1], split[0]);
                    User buddy = this.buddies.get(message.getFrom());
                    Session session = new Session(sessionId, buddy, sk);
                    this.openedSessions.add(session);
                    break;

                case "Message":
                    ChatMessage chatMessage = this.chatMessageByter.fromBytes(
                            message.getBody());
                    Session chatSession = this.sessions.get(
                            chatMessage.getSession());
                    //LOG.info(chatMessage.toString());
                    if (chatSession != null) {
                        String text = chatSession.getProcess().decrypt(
                                chatMessage.getText());
                        chatSession.addMessage(text);
                        //LOG.info(text);
                    }
                    break;

                default:
                    //LOG.warning("Wrong Command!");
                    break;
            }
        }
    }

    public String receiveChatMessage(final Long session) {
        String text = null;
        Session chatSession = this.sessions.get(session);
        if (chatSession != null) {
            Optional<String> incoming = chatSession.getMessage();
            if (incoming.isPresent()) {
                text = incoming.get();
                this.checkEndMessage(text, chatSession);
            }
        }
        return text;
    }

    private void checkEndMessage(final String text, final Session chatSession) {
        if (text.equals("end")) {
            this.closedSessions.add(chatSession);
        }
    }

    public int sessionCount() {
        return this.sessions.size();
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

    public User getIdentity() {
        return identity;
    }

    public Map<Long, Session> getSessions() {
        return sessions;
    }

    public void end() {
        this.listener.shutdown();
    }

}
