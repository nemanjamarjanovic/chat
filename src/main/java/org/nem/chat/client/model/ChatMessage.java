package org.nem.chat.client.model;

import java.io.Serializable;

/**
 *
 * @author Nemanja Marjanović
 */
public class ChatMessage implements Serializable {

    private final Long session;
    private final byte[] text;

    public ChatMessage(Long session, byte[] text) {
        this.session = session;
        this.text = text;
    }

    public Long getSession() {
        return session;
    }

    public byte[] getText() {
        return text;
    }

    @Override
    public String toString() {
        return "ChatMessage{" + "session=" + session + ", text=" + text + '}';
    }

}
