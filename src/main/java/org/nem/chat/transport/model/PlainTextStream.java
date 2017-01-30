package org.nem.chat.transport.model;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

/**
 *
 * @author Nemanja Marjanović
 */
public class PlainTextStream implements TransportStream {

    private final PrintWriter out;
    private final BufferedReader in;

    public PlainTextStream() {
        try {
            Socket socket = new Socket("localhost", 9011);
            this.out = new PrintWriter(socket.getOutputStream(), true);
            this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        } catch (final IOException exception) {
            throw new RuntimeException(exception.getMessage());
        }
    }

    public PlainTextStream(final Socket socket) {
        try {
            this.out = new PrintWriter(socket.getOutputStream(), true);
            this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        } catch (final IOException exception) {
            throw new RuntimeException(exception.getMessage());
        }
    }

    @Override
    public void writeBytes(final byte[] data) {
        this.writeString(new String(data, StandardCharsets.UTF_8));
    }

    @Override
    public void writeString(final String data) {
        this.out.println(data);
    }

    @Override
    public byte[] readBytes() {
        return this.readString().getBytes(StandardCharsets.UTF_8);
    }

    @Override
    public String readString() {
        try {
            return this.in.readLine();
        } catch (final IOException exception) {
            throw new RuntimeException(exception.getMessage());
        }
    }

    public void end() {
        try {
            this.out.close();
            this.in.close();
        } catch (final IOException exception) {
            throw new RuntimeException(exception.getMessage());
        }
    }

}
