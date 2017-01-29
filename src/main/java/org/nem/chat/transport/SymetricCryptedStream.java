package org.nem.chat.transport;

import java.nio.charset.StandardCharsets;
import org.nem.chat.crypto.service.SymetricProcess;

/**
 *
 * @author Nemanja Marjanović
 */
public class SymetricCryptedStream implements TransportStream {

    private final TransportStream transportStream;
    private final SymetricProcess symetricProcess;

    public SymetricCryptedStream(final TransportStream transportStream,
            final SymetricProcess symetricProcess) {
        this.transportStream = transportStream;
        this.symetricProcess = symetricProcess;
    }

    @Override
    public void writeString(final String data) {
        this.transportStream.writeBytes(this.symetricProcess.encrypt(data));
    }

    @Override
    public void writeBytes(final byte[] data) {
        this.writeString(new String(data, StandardCharsets.UTF_8));
    }

    @Override
    public String readString() {
        return this.symetricProcess.decrypt(this.transportStream.readBytes());
    }

    @Override
    public byte[] readBytes() {
        return this.readString().getBytes(StandardCharsets.UTF_8);
    }

}
