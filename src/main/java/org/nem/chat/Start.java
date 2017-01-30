package org.nem.chat;

import org.nem.chat.client.form.ClientWindow;
import org.nem.chat.server.ChatServer;

/**
 *
 * @author nemanja.marjanovic
 */
public class Start {

    public static void main(String[] args) {
        try {
            switch (args[0]) {
                case "server":
                    new ChatServer().listen();
                    break;
                case "client":
                    new ClientWindow().setVisible(true);
                    break;
            }

        } catch (Exception e) {
        }
    }
}
