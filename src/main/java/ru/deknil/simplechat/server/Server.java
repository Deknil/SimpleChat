package ru.deknil.simplechat.server;

import ru.deknil.simplechat.Logger;
import ru.deknil.simplechat.network.ITcpConnectionEvent;
import ru.deknil.simplechat.network.TcpConnection;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;

/**
 * Author: Deknil
 * GitHub: <a href=https://github.com/Deknil>https://github.com/Deknil</a>
 * Date: 09.03.2024
 * Description: The Server class represents a server application for Simple Chat.
 * <p>SimpleChat © 2024. All rights reserved.</p>
 */
public class Server implements ITcpConnectionEvent {
    /**
     * The port number for the server connection.
     */
    public static final int PORT = 9999;

    private final ArrayList<TcpConnection> connections = new ArrayList<>(); // Represents the list of active TCP connections

    /**
     * Main method to start the server application.
     *
     * @param args command-line arguments
     */
    public static void main(String[] args) {
        new Server();
    }

    /**
     * Constructs a Server object and initializes the server functionality.
     */
    private Server() {
        try {
            try (ServerSocket serverSocket = new ServerSocket(PORT)) {
                Logger.print("The server has been successfully launched!");
                while (true) {
                    try {
                        TcpConnection connection = new TcpConnection(this, serverSocket.accept());
                        connections.add(connection);
                    } catch (IOException e) {
                        Logger.print("TCPException: " + e);
                    }
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Send a message to all connections
     * @param message message text
     */
    private void broadcastMessage(String message) {
        Logger.print("Send message: " + message);

        for (TcpConnection connection : connections) {
            connection.sendMessage(message);
        }
    }

    /**
     * New connection event
     * @param connection user connection
     */
    @Override
    public synchronized void onUserConnection(TcpConnection connection) {
        connections.add(connection);
        broadcastMessage("Client connected: " + connection);
    }

    /**
     * New disconnection event
     * @param connection user connection
     */
    @Override
    public synchronized void onUserDisconnection(TcpConnection connection) {
        connections.remove(connection);
        broadcastMessage("Client disconnected: " + connection);
    }

    /**
     * New message receive event
     * @param connection user connection
     * @param message    message text
     */
    @Override
    public synchronized void onMessageReceive(TcpConnection connection, String message) {
        broadcastMessage(message);
    }

    /**
     * New exception receive event
     * @param connection user connection
     * @param exception  exception object
     */
    @Override
    public synchronized void onException(TcpConnection connection, Exception exception) {
        Logger.print(String.format("New exception: '%s' (%s)", exception.getMessage(), connection.toString()));
    }
}