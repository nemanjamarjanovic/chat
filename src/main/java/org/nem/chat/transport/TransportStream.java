package org.nem.chat.transport;

/**
 *
 * @author Nemanja Marjanović
 */
public interface TransportStream {

    public void writeString(final String data);

    public void writeBytes(final byte[] data);

    public String readString();

    public byte[] readBytes();
}
