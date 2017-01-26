package org.nem.chat.protocol.service;

import org.nem.chat.protocol.model.*;

/**
 *
 * @author nemanja.marjanovic
 */
public class MessageBuilder {

    private String type;
    private String to;
    private Header header;
    private byte[] body;

    public static MessageBuilder builder() {
        return new MessageBuilder();
    }

    public MessageBuilder type(final String type) {
        this.type = type;
        return this;
    }

    public MessageBuilder to(final String to) {
        this.to = to;
        return this;
    }

    public MessageBuilder header(final Header header) {
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
