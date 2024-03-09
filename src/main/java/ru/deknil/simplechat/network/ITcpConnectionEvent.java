package ru.deknil.simplechat.network;

/**
 * Author: Deknil
 * GitHub: <a href=https://github.com/Deknil>https://github.com/Deknil</a>
 * Date: 09.03.2024
 * Description: Events related to TCP connection
 * <p>SimpleChat © 2024. All rights reserved.</p>
 */
public interface ITcpConnectionEvent {
    /**
     * New connection event
     * @param connection user connection
     */
    void onUserConnection(TcpConnection connection);

    /**
     * New disconnection event
     * @param connection user connection
     */
    void onUserDisconnection(TcpConnection connection);

    /**
     * New message receive event
     * @param connection user connection
     * @param message message text
     */
    void onMessageReceive(TcpConnection connection, String message);

    /**
     * New exception receive event
     * @param connection user connection
     * @param exception exception object
     */
    void onException(TcpConnection connection, Exception exception);
}
