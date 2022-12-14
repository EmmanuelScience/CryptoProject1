package com.ticketswap.cryptoproject1.emailServer;


import lombok.SneakyThrows;

import java.net.ServerSocket;
import java.net.Socket;

public class EmailServer {
    @SneakyThrows
    public static void main(String[] args) {
        // Create a new server socket
        ServerSocket serverSocket = new ServerSocket(8081);

        // Keep listening for incoming connections
        while (true) {
            // Accept an incoming connection
            Socket socket = serverSocket.accept();

            // Create a new thread to handle the incoming connection
            Thread thread = new Thread(new ConnectionHandler(socket));

            // Start the thread
            thread.start();
        }
    }
}
