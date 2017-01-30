package org.nem.chat.client.service;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.nem.chat.server.ChatServer;

/**
 *
 * @author nemanja.marjanovic
 */
public class ChatIT {

    private ExecutorService serverService;

    @Before
    public void setUp() {
        ChatServer chatServer = new ChatServer();
        serverService = Executors.newSingleThreadExecutor();
        serverService.submit(chatServer::listen);
    }

    @After
    public void tearDown() {
        serverService.shutdown();
    }

    @Test
    public void testSendChatMessage() throws InterruptedException {

        System.out.println("SendChatMessage test");
        String expectedMessage = "Hello chat world!!!";
        String expectedResponse = "Hello chat world!!! Response";

        Chat chat1 = new Chat();
        Chat chat2 = new Chat();
        Chat chat3 = new Chat();

        chat1.login("User1");
        chat2.login("User2");
        chat3.login("User3");

        Thread.sleep(3000);

        chat1.getAvailableBuddies();
        chat2.getAvailableBuddies();
        chat3.getAvailableBuddies();

        Thread.sleep(1000);

        chat1.getAvailableBuddies().stream().forEach(u -> chat1.openChatSession(u));
        chat2.getAvailableBuddies().stream().forEach(u -> chat2.openChatSession(u));
        chat3.getAvailableBuddies().stream().forEach(u -> chat3.openChatSession(u));

        Thread.sleep(3000);

        chat1.getOpenedSession();
        chat1.getOpenedSession();
        chat2.getOpenedSession();
        chat2.getOpenedSession();
        chat3.getOpenedSession();
        chat3.getOpenedSession();

        Thread.sleep(3000);

        System.out.println(chat1.sessionCount());
        System.out.println(chat2.sessionCount());
        System.out.println(chat3.sessionCount());

        chat1.getSessions().values().stream()
                .map(s -> s.getId())
                .forEach(id -> chat1.sendChatMessage(id, expectedMessage));
        chat2.getSessions().values().stream()
                .map(s -> s.getId())
                .forEach(id -> chat2.sendChatMessage(id, expectedMessage));
        chat3.getSessions().values().stream()
                .map(s -> s.getId())
                .forEach(id -> chat3.sendChatMessage(id, expectedMessage));

        Thread.sleep(10000);

        chat1.getSessions().values().stream()
                .map(s -> chat1.receiveChatMessage(s.getId()))
                .filter(m -> m != null)
                .forEach(System.out::println);
        chat2.getSessions().values().stream()
                .map(s -> chat2.receiveChatMessage(s.getId()))
                .filter(m -> m != null)
                .forEach(System.out::println);
        chat3.getSessions().values().stream()
                .map(s -> chat3.receiveChatMessage(s.getId()))
                .filter(m -> m != null)
                .forEach(System.out::println);
        chat1.getSessions().values().stream()
                .map(s -> chat1.receiveChatMessage(s.getId()))
                .filter(m -> m != null)
                .forEach(System.out::println);
        chat2.getSessions().values().stream()
                .map(s -> chat2.receiveChatMessage(s.getId()))
                .filter(m -> m != null)
                .forEach(System.out::println);
        chat3.getSessions().values().stream()
                .map(s -> chat3.receiveChatMessage(s.getId()))
                .filter(m -> m != null)
                .forEach(System.out::println);

        Thread.sleep(3000);
    }
}
