package org.nem.chat.protocol.service;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.logging.Logger;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

/**
 *
 * @author Nemanja Marjanović
 */
public class SymetricProcess {

    private static final Logger LOG = Logger.getLogger(SymetricProcess.class.getName());

    private SecretKeySpec secretKey;
    private Cipher cipher;

    public SymetricProcess(final String algorithm, final String key) {
        try {
            this.secretKey = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), algorithm);
            this.cipher = Cipher.getInstance(algorithm);
        } catch (Exception e) {
            LOG.severe(e.getMessage());
        }
    }

    public String encrypt(final String original) {
        String message = original;
        try {
            //LOG.info("ORIGINAL: [" + this.original + "]");
            this.cipher.init(Cipher.ENCRYPT_MODE, this.secretKey);
            byte[] crypted = this.cipher.doFinal(original.getBytes(StandardCharsets.UTF_8));
            byte[] base64 = Base64.getEncoder().encode(crypted);
            message = new String(base64, StandardCharsets.UTF_8);
            //LOG.info("CRYPTED: [" + message + "]");
        } catch (Exception e) {
            LOG.severe(e.getMessage());
        }
        return message;
    }

    public String decrypt(final String original) {
        String message = original;
        try {
            //LOG.info("CRYPTED: [" + this.original + "]");
            this.cipher.init(Cipher.DECRYPT_MODE, this.secretKey);
            byte[] base64 = Base64.getDecoder().decode(original.getBytes(StandardCharsets.UTF_8));
            byte[] decrypted = this.cipher.doFinal(base64);
            message = new String(decrypted, StandardCharsets.UTF_8);
            //LOG.info("ORIGINAL: [" + message + "]");
        } catch (Exception e) {
            LOG.severe(e.getMessage());
        }
        return message;
    }

}
