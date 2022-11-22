package com.ticketswap.cryptoproject1.chatUtils;

import lombok.SneakyThrows;

import java.io.*;
import java.net.*;

/**
 * This thread is responsible for reading server's input and printing it
 * to the console.
 * It runs in an infinite loop until the client disconnects from the server.
 *
 * @author www.codejava.net
 */
public class ReadThread extends Thread {
    private BufferedReader reader;
    private final ChatClient client;

    public ReadThread(Socket socket, ChatClient client) {
        this.client = client;
        try {
            InputStream input = socket.getInputStream();
            reader = new BufferedReader(new InputStreamReader(input));
        } catch (IOException ex) {
            System.out.println("Error getting input stream: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    @SneakyThrows
    public void run() {
        try {
            String message = reader.readLine();
            String userName = message.split(" ")[0];
            String publicKey = message.split(" ")[1];
            client.getRSA().setPeerPublicKey(publicKey);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        while (!WriteThread.EXIT) {
            try {
                String response = reader.readLine();
                System.out.println("\n" + client.getRSA().decrypt(response));

                // prints the username after displaying the server's message
                if (client.getUserName() != null) {
                    System.out.print("[" + client.getUserName() + "]: ");
                }
            } catch (IOException ex) {
                System.out.println("Error reading from server: " + ex.getMessage());
                ex.printStackTrace();
                break;
            }
        }
    }
}
