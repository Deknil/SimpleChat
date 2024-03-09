package ru.deknil.simplechat.network;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

/**
 * Author: Deknil
 * GitHub: <a href=https://github.com/Deknil>https://github.com/Deknil</a>
 * Date: 09.03.2024
 * Description: The TcpConnection class represents a TCP connection with input and output streams.
 * <p>SimpleChat © 2024. All rights reserved.</p>
 */
public class TcpConnection implements AutoCloseable {
    private final Socket socket; // Represents the socket for the TCP connection[1]
    private final BufferedReader in; // Represents the input stream reader for the connection[1]
    private final BufferedWriter out; // Represents the output stream writer for the connection[1]
    private final ITcpConnectionEvent eventHandler; // Represents the event handler for the TCP connection
    private final Thread connectionThread; // Represents the thread for handling the connection

    /**
     * Constructs a TcpConnection object with the specified event handler, IP address, and port.
     * @param handler the event handler for the TCP connection
     * @param ip the IP address to connect to
     * @param port the port number to connect to
     * @throws IOException if an I/O error occurs when creating the socket
     */
    public TcpConnection(ITcpConnectionEvent handler, String ip, int port) throws IOException {
        this(handler, new Socket(ip, port));
    }

    /**
     * Constructs a TcpConnection object with the specified event handler and socket.
     * @param handler the event handler for the TCP connection
     * @param socket the socket for the TCP connection
     * @throws IOException if an I/O error occurs when creating input/output streams
     */
    public TcpConnection(ITcpConnectionEvent handler, Socket socket) throws IOException {
        this.eventHandler = handler;
        this.socket = socket;
        this.in = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
        this.out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8));
        this.connectionThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    eventHandler.onUserConnection(TcpConnection.this);
                    while (!connectionThread.isInterrupted()) {
                        eventHandler.onMessageReceive(TcpConnection.this, in.readLine());
                    }
                } catch (IOException e) {
                    eventHandler.onException(TcpConnection.this, e);
                } finally {
                    eventHandler.onUserDisconnection(TcpConnection.this);
                }
            }
        });
        this.connectionThread.start();
    }

    /**
     * Sends a message over the TCP connection.
     * @param text the text message to send
     */
    public synchronized void sendMessage(String text) {
        try {
            out.write(text + "\r\n");
            out.flush();
        } catch (Exception e) {
            eventHandler.onException(this, e);
            close();
        }
    }

    /**
     * Returns a string representation of the TcpConnection object.
     * @return a string representing the IP address and port of the TCP connection
     */
    @Override
    public String toString() {
        return "TcpConnection: " + socket.getInetAddress() + ":" + socket.getPort();
    }

    /**
     * Closes the TCP connection by closing input/output streams and interrupting the connection thread.
     */
    @Override
    public synchronized void close() {
        try {
            connectionThread.interrupt();
            in.close();
            out.close();
            socket.close();
        } catch (Exception e) {
            eventHandler.onException(this, e);
        }
    }
}