package org.nem.chat.crypto.service;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import org.nem.chat.crypto.model.SymetricKey;

/**
 *
 * @author Nemanja Marjanović
 */
public class SymetricProcess {

    private final SymetricKey symetricKey;
    private final SecretKeySpec secretKey;
    private final Cipher cipher;

    public SymetricProcess() {
        this(new SymetricKey());
    }

    public SymetricProcess(final SymetricKey symetricKey) {
        this.symetricKey = symetricKey;
        try {
            this.secretKey = new SecretKeySpec(
                    this.symetricKey.getSessionKey().getBytes(StandardCharsets.UTF_8),
                    this.symetricKey.getSymetric());
            this.cipher = Cipher.getInstance(this.symetricKey.getSymetric());
        } catch (Exception exception) {
            throw new RuntimeException(exception.getMessage());
        }
    }

    public byte[] encrypt(final String original) {
        byte[] message = null;
        try {
            this.cipher.init(Cipher.ENCRYPT_MODE, this.secretKey);
            byte[] crypted = this.cipher.doFinal(original.getBytes(StandardCharsets.UTF_8));
            message = Base64.getEncoder().encode(crypted);
        } catch (Exception exception) {
            exception.printStackTrace();
            throw new RuntimeException(exception.getMessage());
        }
        return message;
    }

    public String decrypt(final byte[] original) {
        String message = null;
        try {
            this.cipher.init(Cipher.DECRYPT_MODE, this.secretKey);
            byte[] base64 = Base64.getDecoder().decode(original);
            byte[] decrypted = this.cipher.doFinal(base64);
            message = new String(decrypted, StandardCharsets.UTF_8);
        } catch (Exception exception) {
            exception.printStackTrace();
            throw new RuntimeException(exception.getMessage());
        }
        return message;
    }

    public SymetricKey getSymetricKey() {
        return symetricKey;
    }

    @Override
    public String toString() {
        return "SymetricProcess{" + "symetricKey=" + symetricKey + '}';
    }

}
