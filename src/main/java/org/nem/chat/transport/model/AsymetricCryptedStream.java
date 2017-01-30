package org.nem.chat.transport.model;

import java.nio.charset.StandardCharsets;
import java.security.PrivateKey;
import java.security.PublicKey;
import org.nem.chat.crypto.service.Envelope;

/**
 *
 * @author Nemanja Marjanović
 */
public class AsymetricCryptedStream implements TransportStream {

    private final TransportStream transportStream;
    private final PublicKey destination;
    private final PrivateKey source;

    public AsymetricCryptedStream(TransportStream transportStream,
            PublicKey destination, PrivateKey source) {
        this.transportStream = transportStream;
        this.destination = destination;
        this.source = source;
    }

    @Override
    public void writeBytes(final byte[] data) {
        this.transportStream.writeBytes(Envelope.fromByte(data).pack(destination));
    }

    @Override
    public byte[] readBytes() {
        return Envelope.fromByte(this.transportStream.readBytes()).unpack(source);
    }

    @Override
    public void writeString(final String data) {
        this.writeBytes(data.getBytes(StandardCharsets.UTF_8));
    }

    @Override
    public String readString() {
        return new String(this.readBytes(), StandardCharsets.UTF_8);
    }

}
