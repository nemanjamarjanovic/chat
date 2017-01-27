package org.nem.chat.protocol.service;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.security.MessageDigest;
import java.util.Base64;
import javax.crypto.Cipher;
import org.nem.chat.protocol.model.Message;

/**
 *
 * @author Nemanja Marjanović
 */
public class HashedMessage {

    private final byte[] hash;

    public HashedMessage(final Message message) {
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("MD5");
            this.hash = messageDigest.digest(Message.BYTER.toBytes(message));
        } catch (Exception exception) {
            throw new RuntimeException(exception.getMessage());
        }
    }

    public byte[] signature(final Key key) {
        try {
            Cipher cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.ENCRYPT_MODE, key);
            return Base64.getEncoder().encode(cipher.doFinal(hash));
        } catch (Exception exception) {
            throw new RuntimeException(exception.getMessage());
        }
    }

    public boolean verifySignature(final Key key, final byte[] signature) {
        try {
            Cipher cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.DECRYPT_MODE, key);
            byte[] decoded = cipher.doFinal(Base64.getDecoder().decode(signature));
            return byteToString(decoded).equals(byteToString(this.hash));
        } catch (Exception exception) {
            throw new RuntimeException(exception.getMessage());
        }
    }

    private String byteToString(final byte[] byteArray) {
        return new String(byteArray, StandardCharsets.UTF_8);
    }

}
