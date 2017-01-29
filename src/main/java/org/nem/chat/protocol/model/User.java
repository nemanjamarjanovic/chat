package org.nem.chat.protocol.model;

import java.io.Serializable;
import java.security.PublicKey;

/**
 *
 * @author nemanja.marjanovic
 */
public class User implements Serializable {

    private Long id;
    private String name;
    private PublicKey publicKey;

    public User() {
    }

    public User(Long id, String name, PublicKey publicKey) {
        this.id = id;
        this.name = name;
        this.publicKey = publicKey;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public PublicKey getPublicKey() {
        return publicKey;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPublicKey(PublicKey publicKey) {
        this.publicKey = publicKey;
    }

    @Override
    public String toString() {
        return this.name;
    }

}
