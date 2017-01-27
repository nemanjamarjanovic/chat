package org.nem.chat.protocol.model;

/**
 *
 * @author nemanja.marjanovic
 */
public class MessageBuilder {

    private byte[] serverHeader;
    private byte[] header;
    private byte[] body;

    public static MessageBuilder builder() {
        return new MessageBuilder();
    }

    public MessageBuilder serverHeader(final byte[] serverHeader) {
        this.serverHeader = serverHeader;
        return this;
    }

    public MessageBuilder header(final byte[] header) {
        this.header = header;
        return this;
    }

    public MessageBuilder body(final byte[] body) {
        this.body = body;
        return this;
    }

    public Message build() {
        return new Message(serverHeader, header, body);
    }
}
