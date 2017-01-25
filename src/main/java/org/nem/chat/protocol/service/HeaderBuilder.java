package org.nem.chat.protocol.service;

import org.nem.chat.protocol.model.*;

/**
 *
 * @author nemanja.marjanovic
 */
public class HeaderBuilder {

    private String action;
    private String name;
    private String publickey;
    private String userid;
    private String from;
    private String sessionid;
    private String sessionKey;
    private String hashAlg;
    private String symetricAlg;

    public static HeaderBuilder builder() {
        return new HeaderBuilder();
    }

    public HeaderBuilder action(final String action) {
        this.action = action;
        return this;
    }

    public HeaderBuilder name(final String name) {
        this.name = name;
        return this;
    }

    public HeaderBuilder publickey(final String publickey) {
        this.publickey = publickey;
        return this;
    }

    public HeaderBuilder userid(final String userid) {
        this.userid = userid;
        return this;
    }

    public HeaderBuilder from(final String from) {
        this.from = from;
        return this;
    }

    public HeaderBuilder sessionid(final String sessionid) {
        this.sessionid = sessionid;
        return this;
    }

    public HeaderBuilder sessionKey(final String sessionKey) {
        this.sessionKey = sessionKey;
        return this;
    }

    public HeaderBuilder hashAlg(final String hashAlg) {
        this.hashAlg = hashAlg;
        return this;
    }

    public HeaderBuilder symetricAlg(final String symetricAlg) {
        this.symetricAlg = symetricAlg;
        return this;
    }

    public Header build() {
        return new Header(action, name, publickey, userid, from, sessionid,
                sessionKey, hashAlg, symetricAlg);
    }
}