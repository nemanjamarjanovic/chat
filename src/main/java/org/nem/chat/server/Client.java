package org.nem.chat.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.List;
import java.util.Queue;
import java.util.Random;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import org.nem.chat.protocol.model.Header;
import org.nem.chat.protocol.model.Message;
import org.nem.chat.protocol.model.User;
import org.nem.chat.protocol.model.UserList;
import org.nem.chat.protocol.service.ByteMessage;
import org.nem.chat.protocol.service.HeaderBuilder;
import org.nem.chat.protocol.service.MessageBuilder;

/**
 *
 * @author nemanja.marjanovic
 */
public class Client {

    private static final Logger LOG = Logger.getLogger("SERVER");
    public final Queue<String> OUTBOX = new ConcurrentLinkedQueue<>();

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
                ByteMessage byteMessage = ByteMessage.fromBytes(message.getBytes());
                Message received = byteMessage.getMessage();
                LOG.info(received.toString());
                Header header;
                Message response;
                ByteMessage byteResponse;

                if (received.getType().equals("Server")) {
                    switch (received.getHeader().getAction()) {

                        case "login":
                            this.name = received.getHeader().getName();
                            header = HeaderBuilder.builder().action("login")
                                    .userid(this.id.toString()).build();
                            response = MessageBuilder.builder().header(header).build();
                            byteResponse = ByteMessage.fromMessage(response);
                            this.out.println(new String(byteResponse.getBytes()));
                            LOG.info(response.toString());
                            break;

                        case "logout":
                            ChatServer.CLIENTS.remove(
                                    Long.parseLong(received.getHeader().getUserid()));
                            break;

                        case "users":
                            List<User> list = ChatServer.CLIENTS.keySet().stream()
                                    .map(k -> ChatServer.CLIENTS.get(k))
                                    .filter(client -> client.name != null)
                                    .map(client -> new User(client.id, client.name, client.publicKey))
                                    .collect(Collectors.toList());
                            LOG.info(list.toString());
                            header = HeaderBuilder.builder().action("users").build();
                            response = MessageBuilder.builder().header(header)
                                    .body(UserList.to(new UserList(list))).build();
                            byteResponse = ByteMessage.fromMessage(response);
                            this.out.println(new String(byteResponse.getBytes()));
                            break;

                        case "chat":

                            //ChatServer.INBOX.add(received);
                            break;

                        default:
                            break;
                    }
                } else {
                    ChatServer.CLIENTS.get(received.getTo()).OUTBOX
                            .add(new String(byteMessage.getBytes()));
                }
            }
        } catch (IOException | ClassNotFoundException e) {
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

}
