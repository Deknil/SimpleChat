package ru.deknil.simplechat;

import ru.deknil.simplechat.client.Client;
import ru.deknil.simplechat.server.Server;

/**
 * Author: Deknil
 * GitHub: <a href=https://github.com/Deknil>https://github.com/Deknil</a>
 * Date: 09.03.2024
 * Description: Application entry point, using the '--server' and '--client' flags can be launched in different modes
 * <p>SimpleChat © 2024. All rights reserved.</p>
 */
public class Main {
    /**
     * Main method to start the application based on the provided flag.
     * @param args command-line arguments
     * @throws Exception if an exception occurs during application initialization
     */
    public static void main(String[] args) throws Exception {
        if (args.length != 1) {
            System.out.println("Wrong flag! Use: '--server' or '--client'");
            return;
        }

        switch (args[0]){
            case "--server" -> Server.main(args);
            case "--client" -> Client.main(args);
            default -> System.out.println("Wrong flag! Use: '--server' or '--client'");
        }
    }
}