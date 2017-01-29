package org.nem.chat.server;

import static java.lang.Math.abs;
import java.net.Socket;
import java.security.PublicKey;
import java.util.Queue;
import java.util.Random;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;
import org.nem.chat.crypto.model.SymetricKey;
import org.nem.chat.crypto.service.SymetricProcess;
import org.nem.chat.protocol.model.ByteSerializer;
import org.nem.chat.protocol.model.Message;
import org.nem.chat.protocol.model.User;
import org.nem.chat.transport.AsymetricCryptedStream;
import org.nem.chat.transport.MessageStream;
import org.nem.chat.transport.PlainTextStream;
import org.nem.chat.transport.SymetricCryptedStream;

/**
 *
 * @author nemanja.marjanovic
 */
public class Client {

    private static final Logger LOG = Logger.getLogger("SERVER");
    public final Queue<String> OUTBOX = new ConcurrentLinkedQueue<>();
    private final Long id;
    private final User user;
    private final MessageStream messageStream;
    private final ScheduledExecutorService listener;

    public Client(final Socket socket) {

        this.id = abs(new Random().nextLong());

        PlainTextStream plainTextStream = new PlainTextStream(socket);

        ByteSerializer<PublicKey> PK_BYTER = new ByteSerializer<>();

        byte[] b1 = plainTextStream.readBytes();
        PublicKey clientPk = PK_BYTER.fromBytes(b1);
        //LOG.info(clientPk.toString());

        byte[] b2 = PK_BYTER.toBytes(ChatServer.KEY.getPublicKey());
        plainTextStream.writeBytes(b2);

        AsymetricCryptedStream asymetricCryptedStream = new AsymetricCryptedStream(
                plainTextStream, clientPk, ChatServer.KEY.getPrivateKey());

        String symKey = asymetricCryptedStream.readString();
        //LOG.info(symKey);
        String symAlg = asymetricCryptedStream.readString();
        //LOG.info(symAlg);

        SymetricCryptedStream symetricCryptedStream = new SymetricCryptedStream(plainTextStream,
                new SymetricProcess(new SymetricKey(symKey, symAlg)));

        symetricCryptedStream.writeString(this.id.toString());
        String name = symetricCryptedStream.readString();

        this.user = new User(this.id, name, clientPk);
        this.messageStream = new MessageStream(symetricCryptedStream);
        ClientStreamListener csl = new ClientStreamListener(user, this.messageStream);
        this.listener = Executors.newSingleThreadScheduledExecutor();
        this.listener.scheduleAtFixedRate(csl::listen, 10, 10, TimeUnit.MILLISECONDS);
        LOG.info("Accepted in: ID-" + this.id + " NAME: " + name + " ALG: " + symAlg);
    }

    public void send(final Message data) {
        this.messageStream.writeMessage(data);
        LOG.info("Forwarded" + data);
    }

    public void end() {
        //this.plainTextStream.end();
        listener.shutdown();
        ChatServer.CLIENTS.remove(this.id);
    }

    public Long getId() {
        return this.id;
    }

    public User getUser() {
        return user;
    }

    @Override
    public String toString() {
        return "Client{" + "id=" + id + '}';
    }

}
