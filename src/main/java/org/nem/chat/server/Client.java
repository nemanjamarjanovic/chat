package org.nem.chat.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
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
import org.nem.chat.protocol.model.HeaderBuilder;
import org.nem.chat.protocol.model.MessageBuilder;

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
        } catch (IOException exception) {
            LOG.severe(exception.getMessage());
            throw new RuntimeException();
        }
    }

    public void receive() {

        String message = null;
        while (true) {

            try {
                message = this.in.readLine();
            } catch (IOException exception) {
                LOG.severe(exception.getMessage());
            }
            if (message == null) {
                break;
            }

            Message receivedMessage = Message.BYTER.fromBytes(message.getBytes(StandardCharsets.UTF_8));
            Header receivedHeader = Header.BYTER.fromBytes(receivedMessage.getHeader());
            Header responseHeader;
            Message responseMessage;

            if (receivedMessage.getType().equals("Server")) {
                switch (receivedHeader.getAction()) {

                    case "login":
                        this.name = receivedHeader.getName();
                        responseHeader = HeaderBuilder.builder().action("login").userid(this.id).build();
                        responseMessage = MessageBuilder.builder().header(Header.BYTER.toBytes(responseHeader)).build();
                        this.send(responseMessage);
                        break;

                    case "logout":
                        ChatServer.CLIENTS.remove(receivedHeader.getUserid());
                        break;

                    case "users":
                        List<User> list = ChatServer.CLIENTS.keySet().stream()
                                .map(k -> ChatServer.CLIENTS.get(k))
                                .filter(client -> client.name != null)
                                .map(client -> new User(client.id, client.name, client.publicKey))
                                .collect(Collectors.toList());
                        responseHeader = HeaderBuilder.builder().action("users").build();
                        responseMessage = MessageBuilder.builder().header(Header.BYTER.toBytes(responseHeader))
                                .body(UserList.BYTER.toBytes(new UserList(list))).build();
                        this.send(responseMessage);
                        break;

                    default:
                        break;
                }
            } else {
                LOG.info(receivedMessage.toString());
                ChatServer.CLIENTS.get(receivedMessage.getTo()).send(receivedMessage);
            }
        }

    }

    private void send(final Message message) {
        this.out.println(new String(Message.BYTER.toBytes(message), StandardCharsets.UTF_8));
    }

    public Long getId() {
        return id;
    }

}
