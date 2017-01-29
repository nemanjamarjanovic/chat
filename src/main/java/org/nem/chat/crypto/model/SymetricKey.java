package org.nem.chat.crypto.model;

import java.io.Serializable;
import java.util.Random;
import java.util.UUID;

/**
 *
 * @author Nemanja Marjanović
 */
public class SymetricKey implements Serializable {

    private final String sessionKey;
    private final String symetric;

    public SymetricKey() {
        if (new Random().nextBoolean()) {
            this.sessionKey = UUID.randomUUID().toString().substring(0, 16);
            this.symetric = "AES";
        } else {
            this.sessionKey = UUID.randomUUID().toString().substring(0, 8);
            this.symetric = "DES";
        }
    }

    public SymetricKey(String sessionKey, String symetric) {
        this.sessionKey = sessionKey;
        this.symetric = symetric;
    }

    public String getSessionKey() {
        return sessionKey;
    }

    public String getSymetric() {
        return symetric;
    }

    @Override
    public String toString() {
        return "SymetricKey{" + "sessionKey=" + sessionKey
                + ", symetric=" + symetric + '}';
    }

}
