package com.ticketswap.cryptoproject1.chatUtils;

import com.ticketswap.cryptoproject1.config.RSA;
import lombok.SneakyThrows;

import java.io.Console;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class WriteThread extends Thread {
    private PrintWriter writer;
    private final Socket socket;
    private final ChatClient client;
    public static boolean EXIT = false;

    public WriteThread(Socket socket, ChatClient client) {
        this.socket = socket;
        this.client = client;

        try {
            OutputStream output = socket.getOutputStream();
            writer = new PrintWriter(output, true);
        } catch (IOException ex) {
            System.out.println("Error getting output stream: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    @SneakyThrows
    public void run() {
        Scanner scanner = new Scanner(System.in);
        String userName_PubKey = client.getUserName() + " " + client.getRSA().getPublicKeyString();
        writer.println(userName_PubKey);

        String text;
        do {
            System.out.print("[" + client.getUserName() + "]: ");
            text = scanner.nextLine();
            String encryptedText = client.getRSA().encrypt(text);
            writer.println(encryptedText);
        } while (!text.equals("bye"));
        EXIT = true;

        try {
            socket.close();
        } catch (IOException ex) {
            System.out.println("Error writing to server: " + ex.getMessage());
        }
    }
}
