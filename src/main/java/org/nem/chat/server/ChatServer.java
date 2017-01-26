package org.nem.chat.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.logging.Logger;

/**
 *
 * @author nemanja.marjanovic
 */
public class ChatServer {

    private static final Logger LOG = Logger.getLogger("SERVER");
    public static final Map<Long, Client> CLIENTS = new ConcurrentHashMap<>();

    public void listen() {
        int port = 9011;
        boolean listening = true;

        LOG.info("Server listening on port: " + port);
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            while (listening) {
                Socket socket = serverSocket.accept();
                Client cs = new Client(socket);
                CLIENTS.put(cs.getId(), cs);
                Executors.newSingleThreadExecutor().execute(cs::receive);
                //Executors.newSingleThreadExecutor().execute(cs::send);
                LOG.info("New client: " + cs);
            }
        } catch (IOException ex) {
            LOG.severe(ex.getMessage());
            System.exit(-1);
        }
    }

    public static void main(String[] args) {
        new ChatServer().listen();
    }
}
