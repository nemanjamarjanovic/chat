package org.nem.chat.protocol.model;

import java.io.Serializable;
import java.nio.charset.StandardCharsets;

/**
 *
 * @author Nemanja Marjanović
 */
public class Message implements Serializable {

    private final String command;
    private final Long from;
    private Long to;
    private byte[] body;
    private byte[] signature;

    public Message(String command, Long from) {
        this.command = command;
        this.from = from;
    }

    public Message(String command, Long from, Long to, byte[] body) {
        this.command = command;
        this.from = from;
        this.to = to;
        this.body = body;
    }

    public String getCommand() {
        return command;
    }

    public Long getFrom() {
        return from;
    }

    public Long getTo() {
        return to;
    }

    public byte[] getBody() {
        return body;
    }

    public void setTo(Long to) {
        this.to = to;
    }

    public void setBody(byte[] body) {
        this.body = body;
    }

    public byte[] getSignature() {
        return signature;
    }

    public void setSignature(byte[] signature) {
        this.signature = signature;
    }

    @Override
    public String toString() {
        return "Message{" + "command=" + command + ", from=" + from + ", to="
                + to + ", body=" + byteArrayString(body) + ", signature="
                + byteArrayString(signature) + '}';
    }

    private String byteArrayString(byte[] byteArray) {
        return (byteArray != null) ? new String(byteArray, StandardCharsets.UTF_8) : "[]";
    }

}
