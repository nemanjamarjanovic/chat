package org.nem.chat.protocol.service;

import java.security.MessageDigest;
import java.util.Arrays;
import java.util.Base64;
import java.util.Random;
import org.nem.chat.protocol.model.ByteSerializer;
import org.nem.chat.protocol.model.Message;

/**
 *
 * @author Nemanja Marjanović
 */
public class HashedMessage {

    private final byte[] hash;
    private String alg;
    private final ByteSerializer<Message> byter = new ByteSerializer<>();

    private HashedMessage(final Message message) {
        try {
            if (message.getHashAlg() != null) {
                this.alg = message.getHashAlg();
            } else {
                this.alg = (new Random().nextBoolean()) ? "MD5" : "SHA-256";
            }
            MessageDigest messageDigest = MessageDigest.getInstance(this.alg);
            Message m = new Message(message.getCommand(), message.getFrom(),
                    message.getTo(), message.getBody());
            byte[] hashed = messageDigest.digest(byter.toBytes(m));;
            this.hash = Base64.getEncoder().encode(hashed);
        } catch (Exception exception) {
            exception.printStackTrace();
            throw new RuntimeException(exception.getMessage());
        }
    }

    public static HashedMessage from(final Message message) {
        return new HashedMessage(message);
    }

    public boolean verify(final byte[] signature) {
        return Arrays.equals(this.hash, signature);
    }

    public String alg() {
        return this.alg;
    }

    public byte[] signature() {
        return this.hash;
    }
}
