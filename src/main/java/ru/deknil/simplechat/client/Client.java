package ru.deknil.simplechat.client;

import ru.deknil.simplechat.network.ITcpConnectionEvent;
import ru.deknil.simplechat.network.TcpConnection;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

/**
 * Author: Deknil
 * GitHub: <a href=https://github.com/Deknil>https://github.com/Deknil</a>
 * Date: 09.03.2024
 * Description: The Client class represents a client application for Simple Chat.
 * <p>SimpleChat © 2024. All rights reserved.</p>
 */
public class Client extends JFrame implements ActionListener, ITcpConnectionEvent {
    /**
     * The IP address of the server.
     */
    public static final String IP = "localhost";

    /**
     * The port number for the server connection.
     */
    public static final int PORT = 9999;

    /**
     * The width of the client window.
     */
    public static final int WIDTH = 600;

    /**
     * The height of the client window.
     */
    public static final int HEIGHT = 400;

    private final JTextArea logArea = new JTextArea(); // Represents the text area for logging messages
    private final JTextField nameInput = new JTextField("James"); // Represents the input field for user name
    private final JTextField messageInput = new JTextField(); // Represents the input field for messages
    private TcpConnection connection; // Represents the TCP connection to the server

    /**
     * Main method to start the client application.
     * @param args command-line arguments
     * @throws Exception if an exception occurs during client initialization
     */
    public static void main(String[] args) throws Exception {
        SwingUtilities.invokeLater(Client::new);
    }

    /**
     * Constructs a Client object and initializes the GUI components.
     */
    private Client() {
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setSize(WIDTH, HEIGHT);
        setLocationRelativeTo(null);
        setAlwaysOnTop(true);
        setVisible(true);
        setTitle("Simple Chat");

        logArea.setEditable(false);
        logArea.setLineWrap(true);

        messageInput.addActionListener(this);

        add(logArea, BorderLayout.CENTER);
        add(messageInput, BorderLayout.SOUTH);
        add(nameInput, BorderLayout.NORTH);

        try {
            connection = new TcpConnection(this, IP, PORT);
        } catch (IOException e) {
            printMessage("Connection exception: " + e);
        }
    }

    /**
     * Prints a message to the log area in a synchronized manner.
     * @param message the message to be printed
     */
    private synchronized void printMessage(String message) {
        SwingUtilities.invokeLater(() -> {
            logArea.append(message + "\n");
            logArea.setCaretPosition(logArea.getDocument().getLength());
        });
    }

    /**
     * Invoked when an action occurs.
     * @param e the event to be processed
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        String message = messageInput.getText();

        if (message.isEmpty()) return;

        messageInput.setText(null);
        connection.sendMessage(nameInput.getText() + ": " + message);
    }

    /**
     * New connection event
     * @param connection user connection
     */
    @Override
    public void onUserConnection(TcpConnection connection) {
        printMessage("Connection ready...");
    }

    /**
     * New disconnection event
     * @param connection user connection
     */
    @Override
    public void onUserDisconnection(TcpConnection connection) {
        printMessage("Connection closed");
    }

    /**
     * New message receive event
     * @param connection user connection
     * @param message    message text
     */
    @Override
    public void onMessageReceive(TcpConnection connection, String message) {
        printMessage(message);
    }

    /**
     * New exception receive event
     * @param connection user connection
     * @param exception  exception object
     */
    @Override
    public void onException(TcpConnection connection, Exception exception) {
        printMessage("Connection exception: " + exception);
    }
}