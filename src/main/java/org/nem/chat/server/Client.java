package org.nem.chat.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Random;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import org.nem.chat.protocol.model.User;
import org.nem.chat.protocol.service.Decoder;
import org.nem.chat.protocol.service.Encoder;

/**
 *
 * @author nemanja.marjanovic
 */
public class Client {

    private static final Logger LOG = Logger.getLogger("SERVER");
    public static final Queue<String> OUTBOX = new ConcurrentLinkedQueue<>();

    private final Long id;
    private final BufferedReader in;
    private final PrintWriter out;
    private String name;
    private String publicKey;

    public Client(final Socket socket) {
        this.id = new Random().nextLong();
        try {
            this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.out = new PrintWriter(socket.getOutputStream(), true);
        } catch (Exception e) {
            LOG.severe(e.getMessage());
            throw new RuntimeException();
        }
    }

    public void receive() {
        String message;
        try {
            while ((message = this.in.readLine()) != null) {

                Map<String, Object> decoded = new Decoder().decodeMap(message);
                Map<String, Object> response = new HashMap();

                switch ((String) decoded.get("action")) {

                    case "login":
                        this.name = (String) decoded.get("name");
                        this.publicKey = (String) decoded.get("publicKey");
                        response.put("action", "login");
                        response.put("id", this.id.toString());
                        this.out.println(new Encoder().encodeMap(response));
                        break;

                    case "logout":
                        ChatServer.CLIENTS.remove((Long) decoded.get("id"));
                        break;

                    case "users":
                        List<User> list = ChatServer.CLIENTS.keySet().stream()
                                .map(k -> ChatServer.CLIENTS.get(k))
                                .filter(client -> client.getName() != null)
                                .map(client -> new User(client.id, client.name, client.publicKey))
                                .collect(Collectors.toList());
                        response.put("action", "users");
                        response.put("users", list);
                        this.out.println(new Encoder().encodeMap(response));
                        break;

                    default:
                        ChatServer.INBOX.add(message);
                        break;
                }
            }
        } catch (IOException e) {
            LOG.severe(e.getMessage());
        }
    }

    public void send() {
        String outgoing = OUTBOX.poll();
        if (outgoing != null) {
            this.out.println(outgoing);
        }
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getPublicKey() {
        return publicKey;
    }

}
