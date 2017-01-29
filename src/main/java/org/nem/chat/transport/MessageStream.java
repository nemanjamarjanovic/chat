package org.nem.chat.transport;

import org.nem.chat.protocol.model.ByteSerializer;
import org.nem.chat.protocol.model.Message;
import org.nem.chat.protocol.model.User;

/**
 *
 * @author Nemanja Marjanović
 */
public class MessageStream implements TransportStream {

    private final TransportStream transportStream;
    private User from;
    private User to;
    private String command;
    private final ByteSerializer<Message> byter = new ByteSerializer<>();

    public MessageStream(final TransportStream transportStream, final User from,
            final User to, final String command) {
        this(transportStream, from, to);
        this.command = command;
    }

    public MessageStream(final TransportStream transportStream, final User from,
            final User to) {
        this(transportStream);
        this.from = from;
        this.to = to;
    }

    public MessageStream(final TransportStream transportStream) {
        this.transportStream = transportStream;
    }

    public void forwardMessage(final Message data) {
        this.transportStream.writeBytes(this.byter.toBytes(data));
    }

    public void writeMessage(final Message data) {
        this.transportStream.writeBytes(this.byter.toBytes(data));
    }

    public Message readMessage() {
        return this.byter.fromBytes(this.transportStream.readBytes());
    }

    public Message readUserMessage() {
        //verify
        return this.byter.fromBytes(this.transportStream.readBytes());
    }

    @Override
    public void writeString(String data) {
        Message message = new Message(data, this.from.getId(), this.to.getId(), null);
        //sign?
        this.transportStream.writeBytes(this.byter.toBytes(message));
    }

    @Override
    public String readString() {
        return null;
    }

    @Override
    public void writeBytes(byte[] data) {
        Message message = new Message(this.command, this.from.getId(), this.to.getId(), data);
        //sign with from.publickey
        this.transportStream.writeBytes(byter.toBytes(message));
    }

    @Override
    public byte[] readBytes() {
        return byter.fromBytes(this.transportStream.readBytes());
    }

}
