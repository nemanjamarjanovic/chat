package org.nem.chat.client.service;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.junit.After;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.Test;
import org.nem.chat.client.model.Session;
import org.nem.chat.protocol.model.User;
import org.nem.chat.server.ChatServer;

/**
 *
 * @author nemanja.marjanovic
 */
public class ChatIT {

    private ExecutorService serverService;
    private Chat instance;

    public ChatIT() {
    }

//    @BeforeClass
//    public static void setUpClass() {
//        ChatServer chatServer = new ChatServer();
//        serverService = Executors.newSingleThreadExecutor();
//        serverService.submit(chatServer::listen);
//    }
//
//    @AfterClass
//    public static void tearDownClass() {
//        serverService.shutdown();
//    }
    @Before
    public void setUp() {
        ChatServer chatServer = new ChatServer();
        serverService = Executors.newSingleThreadExecutor();
        serverService.submit(chatServer::listen);
        instance = new Chat();
    }

    @After
    public void tearDown() {
        instance = null;
        serverService.shutdown();
    }

    @Test
    public void testSendChatMessage() throws InterruptedException {

        System.out.println("SendChatMessage test");
        String expectedMessage = "Hello chat world!!!";
        String expectedResponse = "Hello chat world!!! Response";
        int expectedBuddies = 1;
        int expectedSessions = 1;

        Chat buddyInstance = new Chat();
        this.login(this.instance, 1);
        assertTrue(this.instance.isLogged());

        this.login(buddyInstance, 2);
        assertTrue(buddyInstance.isLogged());

        this.getBuddies(this.instance);
        assertEquals(expectedBuddies, this.instance.getAvailableBuddies().size());

        this.getBuddies(buddyInstance);
        assertEquals(expectedBuddies, buddyInstance.getAvailableBuddies().size());

        this.openSession(this.instance);
        Session openedSession = this.instance.getOpenedSession();
        assertNotNull(openedSession);
        assertEquals(expectedSessions, this.instance.sessionCount());

        Session buddySession = buddyInstance.getOpenedSession();
        assertNotNull(buddySession);
        assertEquals(expectedSessions, buddyInstance.sessionCount());

        this.instance.sendChatMessage(openedSession.getId(), expectedMessage);
        this.instance.sendChatMessage(openedSession.getId(), expectedMessage);
        this.instance.sendChatMessage(openedSession.getId(), expectedMessage);
        buddyInstance.sendChatMessage(openedSession.getId(), expectedResponse);
        buddyInstance.sendChatMessage(openedSession.getId(), expectedResponse);
        buddyInstance.sendChatMessage(openedSession.getId(), expectedResponse);
        Thread.sleep(1000);

        String actualMessage = buddyInstance.receiveChatMessage(openedSession.getId());
        String actualResponse = this.instance.receiveChatMessage(openedSession.getId());
        assertEquals(expectedMessage, actualMessage);
        assertEquals(expectedResponse, actualResponse);

        actualMessage = buddyInstance.receiveChatMessage(openedSession.getId());
        actualResponse = this.instance.receiveChatMessage(openedSession.getId());
        assertEquals(expectedMessage, actualMessage);
        assertEquals(expectedResponse, actualResponse);

        actualMessage = buddyInstance.receiveChatMessage(openedSession.getId());
        actualResponse = this.instance.receiveChatMessage(openedSession.getId());
        assertEquals(expectedMessage, actualMessage);
        assertEquals(expectedResponse, actualResponse);

        // nema vise poruka
        actualMessage = buddyInstance.receiveChatMessage(openedSession.getId());
        actualResponse = this.instance.receiveChatMessage(openedSession.getId());
        assertNull(expectedMessage, actualMessage);
        assertNull(expectedResponse, actualResponse);
    }

    private void login(final Chat chat, final int i) throws InterruptedException {
        chat.login("User" + i);
        Thread.sleep(100);
    }

    private void logout(final Chat chat) throws InterruptedException {
        chat.logout();
        Thread.sleep(100);
    }

    private void getBuddies(final Chat chat) throws InterruptedException {
        chat.getAvailableBuddies();
        Thread.sleep(100);
        chat.getAvailableBuddies();
        Thread.sleep(100);
    }

    private void openSession(final Chat chat) throws InterruptedException {
        User buddy = chat.getAvailableBuddies().get(0);
        chat.openChatSession(buddy);
        Thread.sleep(1000);
    }

    private void closeSession(final Chat chat, final Long id) throws InterruptedException {
        chat.closeChatSession(id);
        Thread.sleep(1000);
    }

}
