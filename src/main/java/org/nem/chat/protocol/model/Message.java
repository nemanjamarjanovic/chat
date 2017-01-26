package org.nem.chat.protocol.model;

import java.io.Serializable;

/**
 *
 * @author nemanja.marjanovic
 */
public class Message implements Serializable {

    private static final long serialVersionUID = -4505195206914616869L;
    public static ByteSerializer<Message> BYTER = new ByteSerializer<>();

    private final String type;
    private final Long to;
    private final byte[] header;
    private final byte[] body;

    public Message(String type, Long to, byte[] header, byte[] body) {
        this.type = type;
        this.to = to;
        this.header = header;
        this.body = body;
    }

    public String getType() {
        return type;
    }

    public Long getTo() {
        return to;
    }

    public byte[] getHeader() {
        return header;
    }

    public byte[] getBody() {
        return body;
    }

    @Override
    public String toString() {
        return "Message{" + "type=" + type + ", to=" + to + ", header="
                + new String(header) + ", body=" + new String(body) + '}';
    }

}
