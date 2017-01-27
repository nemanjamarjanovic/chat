package org.nem.chat.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import static java.lang.Math.abs;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Queue;
import java.util.Random;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import org.nem.chat.protocol.model.Header;
import org.nem.chat.protocol.model.HeaderBuilder;
import org.nem.chat.protocol.model.Message;
import org.nem.chat.protocol.model.MessageBuilder;
import org.nem.chat.protocol.model.ServerHeader;
import org.nem.chat.protocol.model.User;
import org.nem.chat.protocol.model.UserList;
import org.nem.chat.protocol.service.Envelope;
import org.nem.chat.protocol.service.HashedMessage;

/**
 *
 * @author nemanja.marjanovic
 */
public class Client {

    private static final Logger LOG = Logger.getLogger("SERVER");
    public final Queue<String> OUTBOX = new ConcurrentLinkedQueue<>();

    private final User user;
    private final BufferedReader in;
    private final PrintWriter out;

    public Client(final Socket socket) {
        this.user = new User();
        this.user.setId(abs(new Random().nextLong()));
        try {
            this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.out = new PrintWriter(socket.getOutputStream(), true);
        } catch (IOException exception) {
            throw new RuntimeException(exception.getMessage());
        }
    }

    public void receive() {

        String message = null;
        while (true) {

            try {
                message = this.in.readLine();
            } catch (IOException exception) {
                throw new RuntimeException(exception.getMessage());
            }
            if (message == null) {
                break;
            }

            //LOG.info(message);
            String split[] = message.split(":");
            Message original = Message.BYTER.fromBytes(split[1].getBytes(StandardCharsets.UTF_8));
            Envelope serverHeaderEnvelope = new Envelope(original.getHeader());
            ServerHeader unpackedServerHeader = ServerHeader.BYTER.fromBytes(serverHeaderEnvelope.unpack(
                    ChatServer.SERVER_KEY.getPrivateKey()));

            if (unpackedServerHeader.getType().equals("Client")) {
                this.forwardMessage(original, unpackedServerHeader.getTo());
                continue;
            }

            Envelope headerEnvelope = new Envelope(original.getHeader());
            Header receivedHeader = Header.BYTER.fromBytes(headerEnvelope.unpack(ChatServer.SERVER_KEY.getPrivateKey()));
            HashedMessage hashedMessage = new HashedMessage(original);
            hashedMessage.verifySignature(this.user.getPublicKey(), split[0].getBytes(StandardCharsets.UTF_8));
            Header responseHeader;
            Message responseMessage;

            switch (receivedHeader.getAction()) {

                case "login":
                    this.user.setName(receivedHeader.getName());
                    responseHeader = HeaderBuilder.builder().action("login").userid(this.user.getId()).build();
                    responseMessage = MessageBuilder.builder().header(Header.BYTER.toBytes(responseHeader)).build();
                    this.send(responseMessage);
                    break;

                case "logout":
                    ChatServer.CLIENTS.remove(receivedHeader.getUserid());
                    break;

                case "users":
                    List<User> list = ChatServer.CLIENTS.keySet().stream()
                            .map(k -> ChatServer.CLIENTS.get(k))
                            .map(client -> client.user)
                            .filter(user -> user.getName() != null)
                            .collect(Collectors.toList());
                    responseHeader = HeaderBuilder.builder().action("users").build();
                    responseMessage = MessageBuilder.builder().header(Header.BYTER.toBytes(responseHeader))
                            .body(UserList.BYTER.toBytes(new UserList(list))).build();
                    this.send(responseMessage);
                    break;

                default:
                    break;
            }
        }
    }

    private void forwardMessage(final Message message, final Long to) {
        ChatServer.CLIENTS.get(to).send(message);
    }

    private void send(final Message message) {
        this.out.println(new String(Message.BYTER.toBytes(message), StandardCharsets.UTF_8));
    }

    public Long getId() {
        return this.user.getId();
    }

    @Override
    public String toString() {
        return "Client{" + "user=" + user + ", in=" + in + ", out=" + out + '}';
    }

}
