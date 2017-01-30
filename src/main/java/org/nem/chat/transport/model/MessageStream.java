package org.nem.chat.transport.model;

import org.nem.chat.protocol.model.ByteSerializer;
import org.nem.chat.protocol.model.Message;
import org.nem.chat.protocol.model.User;
import org.nem.chat.protocol.service.HashedMessage;

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
        this.transportStream.writeBytes(this.byter.toBytes(this.sign(data)));
    }

    public void writeMessage(final Message data) {
        this.transportStream.writeBytes(this.byter.toBytes(this.sign(data)));
    }

    @Override
    public void writeBytes(byte[] data) {
        Message message = new Message(this.command, this.from.getId(), this.to.getId(), data);
        this.transportStream.writeBytes(byter.toBytes(this.sign(message)));
    }

    @Override
    public void writeString(String data) {
        Message message = new Message(data, this.from.getId(), this.to.getId(), null);
        this.transportStream.writeBytes(this.byter.toBytes(this.sign(message)));
    }

    private Message sign(final Message data) {
        Message message = new Message(data.getCommand(), data.getFrom(), data.getTo(), data.getBody());
        HashedMessage hash = HashedMessage.from(message);
        message.setHashAlg(hash.alg());
        message.setSignature(hash.signature());
        return message;
    }

    @Override
    public String readString() {
        return null;
    }

    public Message readMessage() {
        return this.byter.fromBytes(this.transportStream.readBytes());
    }

    @Override
    public byte[] readBytes() {
        return byter.fromBytes(this.transportStream.readBytes());
    }

}
