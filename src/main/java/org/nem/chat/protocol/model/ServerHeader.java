package org.nem.chat.protocol.model;

import java.io.Serializable;

/**
 *
 * @author nemanja.marjanovic
 */
public class ServerHeader implements Serializable {

    private static final long serialVersionUID = 2901049363555932128L;
    public static ByteSerializer<ServerHeader> BYTER = new ByteSerializer<>();

    private final String type;
    private final Long to;

    public ServerHeader(String type, Long to) {
        this.type = type;
        this.to = to;
    }

    public String getType() {
        return type;
    }

    public Long getTo() {
        return to;
    }

    @Override
    public String toString() {
        return "ServerHeader{" + "type=" + type + ", to=" + to + '}';
    }

}
