package org.nem.chat.client.model;

import java.util.UUID;

/**
 *
 * @author nemanja.marjanovic
 */
public class SymetricDetails {

    private final String sessionKey;
    private final String hash;
    private final String symetric;

    public SymetricDetails() {
        this.sessionKey = UUID.randomUUID().toString().substring(0, 15);
        this.hash = "MD5";
        this.symetric = "AES";
    }

    public SymetricDetails(String sessionKey, String hash, String symetric) {
        this.sessionKey = sessionKey;
        this.hash = hash;
        this.symetric = symetric;
    }

    public String getSessionKey() {
        return sessionKey;
    }

    public String getHash() {
        return hash;
    }

    public String getSymetric() {
        return symetric;
    }

}
