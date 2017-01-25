package org.nem.chat.protocol.service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.util.Base64;
import org.nem.chat.protocol.model.Message;

/**
 *
 * @author nemanja.marjanovic
 */
public class ByteMessage {

    private final byte[] bytes;
    private final Message message;

    private ByteMessage(byte[] bytes, Message message) {
        this.bytes = bytes;
        this.message = message;
    }

    public static ByteMessage fromBytes(final byte[] source) throws IOException, ClassNotFoundException {
        final byte[] base64Decoded = Base64.getDecoder().decode(source);
        try (ByteArrayInputStream bis = new ByteArrayInputStream(base64Decoded);
                ObjectInput in = new ObjectInputStream(bis)) {

            ByteMessage created = new ByteMessage(source, (Message) in.readObject());
            return created;
        }
    }

    public static ByteMessage fromMessage(final Message message) throws IOException {
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream();
                ObjectOutput out = new ObjectOutputStream(bos)) {
            out.writeObject(message);

            final byte[] base64Encoded = Base64.getEncoder().encode(bos.toByteArray());
            ByteMessage created = new ByteMessage(base64Encoded, message);
            return created;
        }
    }

    public byte[] getBytes() {
        return bytes;
    }

    public Message getMessage() {
        return message;
    }

}
