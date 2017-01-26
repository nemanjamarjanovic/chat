package org.nem.chat.protocol.model;

import org.nem.chat.protocol.model.*;

/**
 *
 * @author nemanja.marjanovic
 */
public class MessageBuilder {

    private String type;
    private Long to;
    private byte[] header;
    private byte[] body;

    public static MessageBuilder builder() {
        return new MessageBuilder();
    }

    public MessageBuilder type(final String type) {
        this.type = type;
        return this;
    }

    public MessageBuilder to(final Long to) {
        this.to = to;
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
        return new Message(type, to, header, body);
    }
}
