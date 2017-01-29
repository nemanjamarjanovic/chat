package org.nem.chat.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;
import org.nem.chat.crypto.model.AsymetricKey;

/**
 *
 * @author nemanja.marjanovic
 */
public class ChatServer {

    private static final Logger LOG = Logger.getLogger("SERVER");
    public static final Map<Long, Client> CLIENTS = new ConcurrentHashMap<>();
    public static final AsymetricKey KEY = new AsymetricKey();

    public void listen() {
        final int port = 9011;
        //LOG.info("Server listening on port: " + port);
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            while (true) {
                Socket socket = serverSocket.accept();
                Client client = new Client(socket);
                CLIENTS.put(client.getId(), client);
                //LOG.info(CLIENTS.toString());
            }
        } catch (IOException exception) {
            throw new RuntimeException(exception.getMessage());
        }
    }

    public static void main(String[] args) {
        new ChatServer().listen();
    }
}
