package com.ticketswap.cryptoproject1.chatUtils;

import com.ticketswap.cryptoproject1.entities.UserType;

import java.net.*;
import java.io.*;

public class UserChatClient implements ChatClient {
    private final String hostname;
    private final int port;
    private String userName;

    public UserChatClient(String hostname, int port) {
        this.hostname = hostname;
        this.port = port;
    }

    public void execute() {
        try {
            Socket socket = new Socket(hostname, port);
            System.out.println("Connected to the chat server");
            new ReadThread(socket, this).start();
            new WriteThread(socket, this).start();
        } catch (UnknownHostException ex) {
            System.out.println("Server not found: " + ex.getMessage());
        } catch (IOException ex) {
            System.out.println("I/O Error: " + ex.getMessage());
        }
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserName() {
        return this.userName;
    }


    public static void main(String[] args) {
        String hostname = "localhost";
        int port = 8080;
        UserChatClient client = new UserChatClient(hostname, port);
        client.execute();
    }

    public UserType getUserType() {
        return UserType.CUSTOMER;
    }
}