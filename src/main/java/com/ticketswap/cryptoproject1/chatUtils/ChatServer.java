package com.ticketswap.cryptoproject1.chatUtils;

import java.io.*;
import java.net.*;
import java.util.*;

/**
 * This is the chat server program.
 * Press Ctrl + C to terminate the program.
 *
 * @author www.codejava.net
 */
public class ChatServer extends Thread {
    private int port;
    private Queue<UserThread> waitingUsers = new LinkedList<>();
    private String adminName;
    private String currentChatPartner;
    private UserThread currentChatPartnerThread;
    private UserThread adminThread;

    public ChatServer(int port) {
        this.port = port;
    }

    public void execute() {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Chat Server is listening on port " + port);
            while (true) {
                Socket socket = serverSocket.accept();
                if(adminThread == null) {
                    System.out.println("New admin connected");
                    adminThread = new UserThread(socket, this);
                    adminThread.start();
                }
                else if (this.currentChatPartnerThread == null) {
                    System.out.println("New user connected");
                    UserThread newUser = new UserThread(socket, this);
                    this.currentChatPartnerThread = newUser;
                    newUser.start();
                } else {
                    System.out.println("New user waiting");
                    UserThread newUser = new UserThread(socket, this);
                    waitingUsers.add(newUser);
                    newUser.sendMessage("You are in the waiting queue", true);
                }
            }
        } catch (IOException ex) {
            System.out.println("Error in the server: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    public static void main(String[] args) {
        int port = 8080;
        ChatServer server = new ChatServer(port);
        server.execute();
    }

    public void setAdminName(String adminName) {
        this.adminName = adminName;
    }

    public String getAdminName() {
        return this.adminName;
    }

    public void setCurrentChatPartner(String currentChatPartner) {
        this.currentChatPartner = currentChatPartner;
    }

    public String getCurrentChatPartner() {
        return this.currentChatPartner;
    }

    public void setCurrentChatPartnerThread(UserThread currentChatPartnerThread) {
        System.out.println("Setting current chat partner thread"+currentChatPartnerThread.getName());
        this.currentChatPartnerThread = currentChatPartnerThread;
    }

    public UserThread getCurrentChatPartnerThread() {
        return this.currentChatPartnerThread;
    }

    public void setAdminThread(UserThread adminThread) {
        this.adminThread = adminThread;
    }

    public UserThread getAdminThread() {
        return this.adminThread;
    }

    public void setWaitingUsers(Queue<UserThread> waitingUsers) {
        this.waitingUsers = waitingUsers;
    }

    public Queue<UserThread> getWaitingUsers() {
        return this.waitingUsers;
    }

    public void broadcast(String message, UserThread excludeUser) throws IOException {
        if (this.currentChatPartnerThread != null && this.currentChatPartnerThread != excludeUser)
            System.out.println("Broadcasting message: "+message + " from user: "+excludeUser.getName() + " to user: "+this.currentChatPartnerThread.getName());
        if (excludeUser == adminThread && currentChatPartnerThread != null) {
            currentChatPartnerThread.sendMessage(message, false);
        } else if (excludeUser == currentChatPartnerThread && adminThread != null) {
            adminThread.sendMessage(message, false);
        }
    }

    /**
     * Stores username of the newly connected client.
     */
    void setUserName(String userName) {
        currentChatPartner = userName;
    }

    /**
     * When a client is disconneted, removes the associated username and UserThread
     */
    void removeUser() {
        currentChatPartner = null;
        currentChatPartnerThread = null;
    }



    /**
     * Returns true if there are other users connected (not count the currently connected user)
     */
    boolean hasUsers() {
        return this.currentChatPartner != null;
    }
}
