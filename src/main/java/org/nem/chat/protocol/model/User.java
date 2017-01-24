package org.nem.chat.protocol.model;

/**
 *
 * @author nemanja.marjanovic
 */
public class User {

    private Long id;
    private String name;
    private String publicKey;

    public User() {
    }

    public User(Long id, String name, String publicKey) {
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

    public String getPublicKey() {
        return publicKey;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPublicKey(String publicKey) {
        this.publicKey = publicKey;
    }

}
