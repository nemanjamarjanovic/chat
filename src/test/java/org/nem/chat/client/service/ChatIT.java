package org.nem.chat.client.service;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.nem.chat.client.model.Session;
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

        chat1.openChatSession(chat2.getIdentity());
        chat1.openChatSession(chat3.getIdentity());
        chat2.openChatSession(chat1.getIdentity());
        chat2.openChatSession(chat3.getIdentity());
        chat3.openChatSession(chat2.getIdentity());
        chat3.openChatSession(chat1.getIdentity());

        Thread.sleep(3000);

        Session s12 = chat1.getOpenedSession();
        Session s13 = chat1.getOpenedSession();
        Session s21 = chat2.getOpenedSession();
        Session s23 = chat2.getOpenedSession();
        Session s31 = chat3.getOpenedSession();
        Session s32 = chat3.getOpenedSession();
 
        Thread.sleep(3000);
        
        System.out.println(chat1.sessionCount());
        System.out.println(chat2.sessionCount());
        System.out.println(chat3.sessionCount());

        chat1.sendChatMessage(s12.getId(), expectedMessage);
        chat1.sendChatMessage(s13.getId(), expectedMessage);
        chat2.sendChatMessage(s21.getId(), expectedMessage);
        chat2.sendChatMessage(s23.getId(), expectedMessage);
        chat3.sendChatMessage(s31.getId(), expectedMessage);
        chat3.sendChatMessage(s32.getId(), expectedMessage);

        Thread.sleep(10000);

        System.out.println(chat1.receiveChatMessage(s12.getId()));
        System.out.println(chat1.receiveChatMessage(s13.getId()));
        System.out.println(chat2.receiveChatMessage(s21.getId()));
        System.out.println(chat2.receiveChatMessage(s23.getId()));
        System.out.println(chat3.receiveChatMessage(s31.getId()));
        System.out.println(chat3.receiveChatMessage(s32.getId()));

    }
}
