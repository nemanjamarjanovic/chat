package org.nem.chat.client.model;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Optional;
import java.util.Queue;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.ConcurrentLinkedQueue;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import org.nem.chat.protocol.model.User;

/**
 *
 * @author nemanja.marjanovic
 */
public class Session {

    private final Long id;
    private final User buddy;
    private final Queue<String> messages = new ConcurrentLinkedQueue<>();
    private final String sessionKey;
    private final String symetric;
    private final SecretKeySpec secretKey;
    private final Cipher cipher;

    public Session(final User buddy) {
        this.id = new Random().nextLong();
        this.buddy = buddy;
        this.sessionKey = UUID.randomUUID().toString().substring(0, 16);
        this.symetric = "AES";
        try {
            this.secretKey = new SecretKeySpec(
                    this.sessionKey.getBytes(StandardCharsets.UTF_8),
                    this.symetric);
            this.cipher = Cipher.getInstance(this.symetric);
        } catch (Exception exception) {
            throw new RuntimeException(exception.getMessage());
        }
    }

    public Session(final Long id, final User buddy,
            final String sessionKey, final String symetric) {
        this.id = id;
        this.buddy = buddy;
        this.sessionKey = sessionKey;
        this.symetric = symetric;
        try {
            this.secretKey = new SecretKeySpec(
                    this.sessionKey.getBytes(StandardCharsets.UTF_8),
                    this.symetric);
            this.cipher = Cipher.getInstance(this.symetric);
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
            throw new RuntimeException(exception.getMessage());
        }
        return message;
    }

    public void addMessage(final String message) {
        this.messages.add(message);
    }

    public Optional<String> getMessage() {
        return Optional.ofNullable(this.messages.poll());
    }

    public Long getId() {
        return id;
    }

    public User getBuddy() {
        return buddy;
    }

    public String getSessionKey() {
        return sessionKey;
    }

    public String getSymetric() {
        return symetric;
    }
}
