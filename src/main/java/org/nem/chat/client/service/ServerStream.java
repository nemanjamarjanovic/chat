package org.nem.chat.client.service;

import java.security.PublicKey;
import java.util.logging.Logger;
import org.nem.chat.crypto.model.AsymetricKey;
import org.nem.chat.crypto.service.SymetricProcess;
import org.nem.chat.protocol.model.ByteSerializer;
import org.nem.chat.transport.AsymetricCryptedStream;
import org.nem.chat.transport.PlainTextStream;
import org.nem.chat.transport.SymetricCryptedStream;
import org.nem.chat.transport.TransportStream;

/**
 *
 * @author Nemanja Marjanović
 */
public class ServerStream implements TransportStream {

    private static final Logger LOG = Logger.getLogger("CLIENT");
    private final Long identity;
    private final TransportStream transportStream;
    private final PublicKey serverPublicKey;

    public ServerStream(final AsymetricKey asymetricKey, final String name) {

        TransportStream plainTextStream = new PlainTextStream();

        //posalji svoj public key
        byte[] b1 = new ByteSerializer<>().toBytes(asymetricKey.getPublicKey());
        plainTextStream.writeBytes(b1);

        //cekaj na serverski public key
        byte[] b2 = plainTextStream.readBytes();
        this.serverPublicKey = new ByteSerializer<>().fromBytes(b2);
        //LOG.info(serverPublicKey.toString());

        TransportStream asymetricCryptedStream = new AsymetricCryptedStream(
                plainTextStream, this.serverPublicKey, asymetricKey.getPrivateKey());

        //kreiranje nove sesije
        SymetricProcess symetricProcess = new SymetricProcess();
        this.transportStream = new SymetricCryptedStream(plainTextStream, symetricProcess);
        //LOG.info(symetricProcess.toString());

        asymetricCryptedStream.writeString(symetricProcess.getSymetricKey().getSessionKey());
        asymetricCryptedStream.writeString(symetricProcess.getSymetricKey().getSymetric());
        this.identity = Long.parseLong(this.transportStream.readString());

        this.transportStream.writeString(name);
        //LOG.info("Logged in: ID-" + this.identity + " NAME: " + name);
    }

    public Long getId() {
        return this.identity;
    }

    public PublicKey getServerPublicKey() {
        return serverPublicKey;
    }

    @Override
    public void writeString(String data) {
        this.transportStream.writeString(data);
    }

    @Override
    public void writeBytes(byte[] data) {
        this.transportStream.writeBytes(data);
    }

    @Override
    public String readString() {
        return this.transportStream.readString();
    }

    @Override
    public byte[] readBytes() {
        return this.transportStream.readBytes();
    }

}
