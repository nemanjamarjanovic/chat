package org.nem.chat.server;

import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import org.nem.chat.protocol.model.ByteSerializer;
import org.nem.chat.protocol.model.Message;
import org.nem.chat.protocol.model.User;
import org.nem.chat.protocol.model.UserList;
import org.nem.chat.transport.MessageStream;

/**
 *
 * @author Nemanja Marjanović
 */
public class ClientStreamListener {

    //private static final Logger LOG = Logger.getLogger("SERVER");
    private final User user;
    private final MessageStream messageStream;

    public ClientStreamListener(final User user, final MessageStream messageStream) {
        this.user = user;
        this.messageStream = messageStream;
    }

    public void listen() {

        Message message = this.messageStream.readMessage();
        //LOG.info("Server New Message(" + user.getId() + "): " + message.toString());

        switch (message.getCommand()) {
            case "Users":
                List<User> list = ChatServer.CLIENTS.keySet().stream()
                        .map(k -> ChatServer.CLIENTS.get(k))
                        .map(client -> client.getUser())
                        .filter(u -> u.getName() != null)
                        .collect(Collectors.toList());
                byte[] b2 = new ByteSerializer<>().toBytes(new UserList(list));
                Message response = new Message("Users", null, null, b2);
                this.messageStream.writeMessage(response);
                break;
            case "End":
                ChatServer.CLIENTS.get(this.user.getId()).end();
                break;
            case "Open":
            case "Close":
            case "Message":
                //LOG.info("Forwarding: " + message.toString());
                ChatServer.CLIENTS.get(message.getTo()).send(message);
                break;
            default:
                //LOG.warning("Wrong Command!");
                break;
        }
    }
}
