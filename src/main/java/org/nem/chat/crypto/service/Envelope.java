package org.nem.chat.crypto.service;

import java.security.Key;
import java.util.Base64;
import javax.crypto.Cipher;

/**
 *
 * @author nemanja.marjanovic
 */
public class Envelope {

    private final byte[] original;

    public Envelope(byte[] original) {
        this.original = original;
    }

    public static Envelope fromByte(byte[] original) {
        return new Envelope(original);
    }

    public byte[] pack(final Key key) {
        try {
            Cipher cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.ENCRYPT_MODE, key);
            return Base64.getEncoder().encode(cipher.doFinal(original));
        } catch (Exception exception) {
            exception.printStackTrace();
            throw new RuntimeException(exception.getMessage());
        }
    }

    public byte[] unpack(final Key key) {
        try {
            Cipher cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.DECRYPT_MODE, key);
            return cipher.doFinal(Base64.getDecoder().decode(original));
        } catch (Exception exception) {
            exception.printStackTrace();
            throw new RuntimeException(exception.getMessage());
        }
    }
}
