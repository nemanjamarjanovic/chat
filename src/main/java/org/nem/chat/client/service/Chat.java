package org.nem.chat.client.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Queue;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executors;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import org.nem.chat.client.model.Session;
import org.nem.chat.client.model.SymetricDetails;
import org.nem.chat.protocol.model.User;
import org.nem.chat.protocol.service.Decoder;
import org.nem.chat.protocol.service.Encoder;

/**
 *
 * @author nemanja.marjanovic
 */
public class Chat {

    private static final Logger LOG = Logger.getLogger("CLIENT");

    private User identity;
    private String privateKey;
    private final Map<Long, User> buddies = new ConcurrentHashMap<>();
    private final Map<Long, Session> sessions = new ConcurrentHashMap<>();
    private final Map<Long, Session> openSessionsRequests = new ConcurrentHashMap<>();
    private final Map<Long, Session> closeSessionsRequests = new ConcurrentHashMap<>();
    private final Queue<Session> openedSessions = new ConcurrentLinkedQueue<>();
    private final Queue<Session> closedSessions = new ConcurrentLinkedQueue<>();
    private final PrintWriter out;
    private final BufferedReader in;
    private final Encoder encoder = new Encoder();
    private final Decoder decoder = new Decoder();

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
        Map<String, String> message = new HashMap();
        message.put("action", "login");
        message.put("name", this.identity.getName());
        message.put("key", this.identity.getPublicKey());
        this.sendMessage(message);
    }

    public void logout() {
        if (isLogged()) {
            Map<String, String> message = new HashMap();
            message.put("action", "logout");
            message.put("id", this.identity.getId().toString());
            this.sendMessage(message);
            identity.setId(null);
        }
    }

    public List<User> getAvailableBuddies() {
        Map<String, String> message = new HashMap();
        message.put("action", "users");
        this.sendMessage(message);
        return this.buddies.keySet()
                .stream()
                .filter(k -> !k.equals(this.identity.getId()))
                .map(k -> this.buddies.get(k))
                .collect(Collectors.toList());
    }

    public void openChatSession(final User buddy) {
        Session newSession = new Session(buddy);
        Map<String, Object> message = new HashMap();
        message.put("action", "session-open");
        message.put("session", newSession);
        message.put("to", buddy.getId());
        message.put("from", this.identity.getId());
        this.sendMessage(message);
        this.openSessionsRequests.put(newSession.getId(), newSession);
    }

    public void closeChatSession(final Long id) {
        if (this.sessions.containsKey(id)) {
            Session sessionToClose = this.sessions.get(id);
            Map<String, Object> message = new HashMap();
            message.put("action", "session-close");
            message.put("session", sessionToClose.getId());
            message.put("to", sessionToClose.getBuddy().getId());
            message.put("from", this.identity.getId());
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
// String output = new SymetricProcess(chatSession.getSymAlg(), chatSession.getKey()).encrypt(msg);
            Map<String, Object> message = new HashMap();
            message.put("action", "chat");
            message.put("session", session);
            message.put("to", chatSession.getBuddy().getId());
            message.put("from", this.identity.getId());
            message.put("text", text);
            this.sendMessage(message);
        }
    }

    public String receiveChatMessage(final Long session) {
        String message = null;
        Session chatSession = this.sessions.get(session);
        Optional<String> incoming = chatSession.getMessage(message);
        if (incoming.isPresent()) {
            // message = new SymetricProcess(chatSession.getSymAlg(), chatSession.getKey()).decrypt(cm.getContent());
        }
        return message;
    }

    private void sendMessage(final Map message) {
        this.out.println(this.encoder.encodeMap(message));
    }

    private void listenForMessages() {
        String fromServer;
        try {
            while ((fromServer = in.readLine()) != null) {
                this.processIncomingMessage(fromServer);
            }
        } catch (IOException ex) {
            LOG.severe(ex.getMessage());
        }
    }

    private void processIncomingMessage(final String message) {
        Map<String, Object> decoded = decoder.decodeMap(message);
        LOG.info(decoded.toString());

        switch ((String) decoded.get("action")) {

            case "chat":
                this.sessions.get(Long.parseLong((String) decoded.get("session")))
                        .addMessage((String) decoded.get("text"));
                break;

            case "login":
                identity.setId(Long.parseLong((String) decoded.get("id")));
                break;

            case "users":
                this.buddies.clear();
                ((List<User>) decoded.get("users")).forEach(user -> {
                    this.buddies.put(user.getId(), user);
                });
                break;

            case "session-open":
                User buddy = this.buddies.get((String) decoded.get("from"));
                Long sessionId = (Long) decoded.get("session");
                SymetricDetails sd = (SymetricDetails) decoded.get("symetric");
                Session newSession = new Session(sessionId, buddy, sd);
                this.openedSessions.add(newSession);
                Map<String, Object> response = new HashMap();
                response.put("action", "session-open-confirm");
                response.put("session", sessionId);
                this.sendMessage(response);

            case "session-open-confirm":
                Long sessionId2 = (Long) decoded.get("session");
                Session newSession2 = this.openSessionsRequests.remove(sessionId2);
                this.openedSessions.add(newSession2);

            case "session-close":
                Long csid = (Long) decoded.get("session");
                Session cs = this.sessions.get(csid);
                this.closedSessions.add(cs);
                Map<String, Object> response2 = new HashMap();
                response2.put("action", "session-close-confirm");
                response2.put("session", csid);
                this.sendMessage(response2);
                break;

            case "session-close-confirm":
                Long csid1 = (Long) decoded.get("session");
                Session cs1 = this.closeSessionsRequests.remove(csid1);
                this.closedSessions.add(cs1);
                break;

            default:
                break;

        }

    }

    public static void main(String[] args) {
        Chat chat = new Chat();
        chat.login("Nemanja" + new Random().nextInt());
        // chat.logout();
        chat.getAvailableBuddies();
    }
}
