package org.nem.chat.protocol.model;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Base64;

/**
 *
 * @author Nemanja Marjanović
 */
public class ByteSerializer<Type> implements Serializable {

    public <Type> Type fromBytes(final byte[] source) {
        final byte[] base64Decoded = Base64.getDecoder().decode(source);
        try (ByteArrayInputStream bis = new ByteArrayInputStream(base64Decoded);
                ObjectInput in = new ObjectInputStream(bis)) {
            return (Type) in.readObject();
        } catch (IOException | ClassNotFoundException exception) {
            throw new RuntimeException(exception.getMessage());
        }
    }

    public byte[] toBytes(final Type source) {
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream();
                ObjectOutput out = new ObjectOutputStream(bos)) {
            out.writeObject(source);
            final byte[] base64Encoded = Base64.getEncoder().encode(bos.toByteArray());
            return base64Encoded;
        } catch (IOException exception) {
            throw new RuntimeException(exception.getMessage());
        }
    }
}
