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
import java.util.List;

/**
 *
 * @author nemanja.marjanovic
 */
public class UserList implements Serializable {

    private static final long serialVersionUID = 7615105865807425418L;

    private List<User> list;

    public UserList(List<User> list) {
        this.list = list;
    }

    public List<User> getList() {
        return list;
    }

    public static UserList from(final byte[] source) throws IOException, ClassNotFoundException {
        final byte[] base64Decoded = Base64.getDecoder().decode(source);
        try (ByteArrayInputStream bis = new ByteArrayInputStream(base64Decoded);
                ObjectInput in = new ObjectInputStream(bis)) {
            return (UserList) in.readObject();
        }
    }

    public static byte[] to(final UserList source) throws IOException {
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream();
                ObjectOutput out = new ObjectOutputStream(bos)) {
            out.writeObject(source);
            final byte[] base64Encoded = Base64.getEncoder().encode(bos.toByteArray());
            return base64Encoded;
        }
    }

    @Override
    public String toString() {
        return "UserList{" + "list=" + list + '}';
    }

}
