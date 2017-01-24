package org.nem.chat.client.model;

import java.util.Optional;
import java.util.Queue;
import java.util.Random;
import java.util.concurrent.ConcurrentLinkedQueue;
import org.nem.chat.protocol.model.User;

/**
 *
 * @author nemanja.marjanovic
 */
public class Session {

    private final Long id;
    private final User buddy;
    private final SymetricDetails symetricDetails;
    private final Queue<String> messages = new ConcurrentLinkedQueue<>();

    public Session(final User buddy) {
        this.id = new Random().nextLong();
        this.buddy = buddy;
        this.symetricDetails = new SymetricDetails();
    }

    public Session(final Long id, final User buddy,
            final SymetricDetails symetricDetails) {
        this.id = id;
        this.buddy = buddy;
        this.symetricDetails = symetricDetails;
    }

    public void addMessage(final String message) {
        this.messages.add(message);
    }

    public Optional<String> getMessage(final String message) {
        return Optional.ofNullable(this.messages.poll());
    }

    public Long getId() {
        return id;
    }

    public User getBuddy() {
        return buddy;
    }

    public SymetricDetails getSymetricDetails() {
        return symetricDetails;
    }

}
