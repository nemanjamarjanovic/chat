package org.nem.chat.client.model;

import static java.lang.Math.abs;
import java.util.Optional;
import java.util.Queue;
import java.util.Random;
import java.util.concurrent.ConcurrentLinkedQueue;
import org.nem.chat.crypto.model.SymetricKey;
import org.nem.chat.crypto.service.SymetricProcess;
import org.nem.chat.protocol.model.User;

/**
 *
 * @author nemanja.marjanovic
 */
public class Session {

    private final Long id;
    private final User buddy;
    private final Queue<String> messages = new ConcurrentLinkedQueue<>();
    private final SymetricProcess process;

    public Session(User buddy) {
        this(abs(new Random().nextLong()), buddy, new SymetricKey());
    }

    public Session(final Long id, final User buddy, final SymetricKey symetricKey) {
        this.id = id;
        this.buddy = buddy;
        this.process = new SymetricProcess(symetricKey);
    }

    public void addMessage(final String message) {
        this.messages.add(message);
    }

    public Optional<String> getMessage() {
        return Optional.ofNullable(this.messages.poll());
    }

    public Long getId() {
        return id;
    }

    public User getBuddy() {
        return buddy;
    }

    public SymetricProcess getProcess() {
        return process;
    }

}
