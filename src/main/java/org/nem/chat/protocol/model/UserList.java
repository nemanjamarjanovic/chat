package org.nem.chat.protocol.model;

import java.io.Serializable;
import java.util.List;

/**
 *
 * @author nemanja.marjanovic
 */
public class UserList implements Serializable {

    private static final long serialVersionUID = 7615105865807425418L;
    public static ByteSerializer<UserList> BYTER = new ByteSerializer<>();

    private final List<User> list;

    public UserList(List<User> list) {
        this.list = list;
    }

    public List<User> getList() {
        return list;
    }

    @Override
    public String toString() {
        return "UserList{" + "list=" + list + '}';
    }

}
