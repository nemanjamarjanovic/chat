package org.nem.chat.protocol.model;

import java.io.Serializable;

/**
 *
 * @author nemanja.marjanovic
 */
public class Message implements Serializable {

    private static final long serialVersionUID = -4505195206914616869L;
    public static ByteSerializer<Message> BYTER = new ByteSerializer<>();

    private final byte[] serverHeader;
    private final byte[] header;
    private final byte[] body;

    public Message(byte[] serverHeader, byte[] header, byte[] body) {
        this.serverHeader = serverHeader;
        this.header = header;
        this.body = body;
    }

    public byte[] getServerHeader() {
        return serverHeader;
    }

    public byte[] getHeader() {
        return header;
    }

    public byte[] getBody() {
        return body;
    }

    @Override
    public String toString() {
        return "Message{"
                + ", serverHeader=" + this.byteArrayString(serverHeader)
                + ", header=" + this.byteArrayString(header)
                + ", body=" + this.byteArrayString(body) + '}';
    }

    private String byteArrayString(final byte[] byteArray) {
        return (byteArray != null) ? new String(byteArray) : "[]";
    }

}
