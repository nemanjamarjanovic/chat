package org.nem.chat.protocol.model;

import java.io.Serializable;

/**
 *
 * @author nemanja.marjanovic
 */
public class Message implements Serializable {

    private static final long serialVersionUID = -4505195206914616869L;

    private final String type;
    private final String to;
    private final Header header;
    private final byte[] body;

    public Message(String type, String to, Header header, byte[] body) {
        this.type = type;
        this.to = to;
        this.header = header;
        this.body = body;
    }

    public String getType() {
        return type;
    }

    public String getTo() {
        return to;
    }

    public Header getHeader() {
        return header;
    }

    public byte[] getBody() {
        return body;
    }

    @Override
    public String toString() {
        return "Message{" + "type=" + type + ", to=" + to + ", header=" + header + ", body=" + body + '}';
    }

}
