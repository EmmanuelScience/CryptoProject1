package com.ticketswap.cryptoproject1.chatUtils;

import java.io.*;
import java.net.*;
import java.util.*;

/**
 * This thread handles connection for each connected client, so the server
 * can handle multiple clients at the same time.
 *
 * @author www.codejava.net
 */
public class UserThread extends Thread {
    private Socket socket;
    private ChatServer server;
    private PrintWriter writer;

    public UserThread(Socket socket, ChatServer server) {
        this.socket = socket;
        this.server = server;
    }

    public void run() {
        try {
            InputStream input = socket.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(input));

            OutputStream output = socket.getOutputStream();
            writer = new PrintWriter(output, true);


            String userName = reader.readLine();
            server.setUserName(userName);

            String serverMessage = "New user connected: " + userName;
            server.broadcast(serverMessage, this);

            String clientMessage;

            do {
                clientMessage = reader.readLine();
                serverMessage = "[" + userName + "]: " + clientMessage;
                server.broadcast(serverMessage, this);

            } while (!clientMessage.equals("bye"));

            server.removeUser();
            socket.close();
            checkWaitingQueue();

        } catch (IOException ex) {
            System.out.println("Error in UserThread: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    public void checkWaitingQueue() {
        if(server.getCurrentChatPartner() != null && server.getWaitingUsers().size() > 0) {
            UserThread chatPartner = server.getWaitingUsers().poll();
            server.setCurrentChatPartnerThread(chatPartner);
            server.getCurrentChatPartnerThread().start();
            server.getCurrentChatPartnerThread().writer.println("You are now chatting with " + server.getCurrentChatPartner());
        } else {
            writer.println("Waiting for a chat partner...");
        }
    }


    /**
     * Sends a message to the client.
     */
    void sendMessage(String message, boolean inQueue) throws IOException {
        if(inQueue) {
            InputStream input = socket.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(input));
            OutputStream output = socket.getOutputStream();
            writer = new PrintWriter(output, true);
        } else {
            writer.println(message);
        }
    }
}
