package org.nem.chat.client.service;

import java.security.PublicKey;
import org.nem.chat.client.model.Session;
import org.nem.chat.protocol.model.Header;
import org.nem.chat.protocol.model.HeaderBuilder;
import org.nem.chat.protocol.model.Message;
import org.nem.chat.protocol.model.MessageBuilder;
import org.nem.chat.protocol.model.ServerHeader;

/**
 *
 * @author Nemanja Marjanović
 */
public class MessageComposer {

    public Message loginMessage(final String name, final PublicKey publicKey) {
        return serverMessage(HeaderBuilder.builder().action("login").name(name).publickey(publicKey).build());
    }

    public Message logoutMessage(final Long id) {
        return serverMessage(HeaderBuilder.builder().action("logout").userid(id).build());
    }

    public Message usersMessage() {
        return serverMessage(HeaderBuilder.builder().action("users").build());
    }

    private Message serverMessage(final Header header) {
        return MessageBuilder.builder().serverHeader(serverHeader("Server", null))
                .header(Header.BYTER.toBytes(header)).build();
    }

    public Message sendOpenSessionMessage(final Session session, final Long id) {
        return sessionMessage(HeaderBuilder.builder().action("session-open").sessionid(session.getId()).sessionKey(session.getSessionKey())
                .symetricAlg(session.getSymetric()).from(id).build(), session.getBuddy().getId());
    }

    public Message sendCloseSessionMessage(final Session session, final Long id) {
        return sessionMessage(HeaderBuilder.builder().action("session-close").sessionid(session.getId()).from(id).build(), session.getBuddy().getId());
    }

    public Message sendOpenSessionConfirmMessage(final Long session, final Long id, final Long to) {
        return sessionMessage(HeaderBuilder.builder().action("session-open-confirm").sessionid(session).from(id).build(), to);
    }

    public Message sendCloseSessionConfirmMessage(final Long session, final Long id, final Long to) {
        return sessionMessage(HeaderBuilder.builder().action("session-close-confirm").sessionid(session).from(id).build(), to);
    }

    public Message sendChatMessage(final Session session, final Long id, final byte[] text) {
        Header header = HeaderBuilder.builder().action("chat").sessionid(session.getId()).from(id).build();
        return MessageBuilder.builder()
                .serverHeader(serverHeader("Client", session.getBuddy().getId()))
                .header(Header.BYTER.toBytes(header)).body(text).build();
    }

    private Message sessionMessage(final Header header, final Long to) {
        return MessageBuilder.builder().serverHeader(serverHeader("Client", to)).header(Header.BYTER.toBytes(header)).build();
    }

    private byte[] serverHeader(final String type, final Long to) {
        return ServerHeader.BYTER.toBytes(new ServerHeader(type, to));
    }
}
