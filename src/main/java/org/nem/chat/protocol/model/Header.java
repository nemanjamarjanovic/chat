package org.nem.chat.protocol.model;

import java.io.Serializable;

/**
 *
 * @author nemanja.marjanovic
 */
public class Header implements Serializable {

    private static final long serialVersionUID = 2901049363555932128L;

    private final String action;
    private final String name;
    private final String publickey;
    private final Long userid;
    private final String from;
    private final Long sessionid;
    private final String sessionKey;
    private final String hashAlg;
    private final String symetricAlg;

    public Header(String action, String name, String publickey, Long userid,
            String from, Long sessionid, String sessionKey, String hashAlg,
            String symetricAlg) {
        this.action = action;
        this.name = name;
        this.publickey = publickey;
        this.userid = userid;
        this.from = from;
        this.sessionid = sessionid;
        this.sessionKey = sessionKey;
        this.hashAlg = hashAlg;
        this.symetricAlg = symetricAlg;
    }

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public String getAction() {
        return action;
    }

    public String getName() {
        return name;
    }

    public String getPublickey() {
        return publickey;
    }

    public Long getUserid() {
        return userid;
    }

    public String getFrom() {
        return from;
    }

    public Long getSessionid() {
        return sessionid;
    }

    public String getSessionKey() {
        return sessionKey;
    }

    public String getHashAlg() {
        return hashAlg;
    }

    public String getSymetricAlg() {
        return symetricAlg;
    }

    @Override
    public String toString() {
        return "Header{" + "action=" + action + ", name=" + name
                + ", publickey=" + publickey + ", userid=" + userid + ", from="
                + from + ", sessionid=" + sessionid + ", sessionKey="
                + sessionKey + ", hashAlg=" + hashAlg + ", symetricAlg="
                + symetricAlg + '}';
    }

}
