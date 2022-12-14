package com.ticketswap.cryptoproject1.utils;

import com.ticketswap.cryptoproject1.crypto.RSA;
import lombok.SneakyThrows;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Date;
import java.util.Properties;

public class EmailUtility {
    RSA rsa = new RSA();
    private final String SERVER_PUBLIC_KEY = "";
    public EmailUtility() {
    }

    @SneakyThrows
    public  void sendMail(String message ) throws MessagingException {
        //Encrypt the message
        rsa.setPeerPublicKey(SERVER_PUBLIC_KEY);
        String encryptedMessage = rsa.encrypt(message);

        Socket socket = new Socket("localhost", 8081);

        // Create a BufferedReader to read from the socket
        BufferedReader reader = new BufferedReader(
                new InputStreamReader(socket.getInputStream()));

        // Create a PrintWriter to write to the socket
        PrintWriter writer = new PrintWriter(
                new OutputStreamWriter(socket.getOutputStream()));

        // Send a message to the server
        writer.println(encryptedMessage);
        writer.flush();


        // Close the socket
        socket.close();
    }
}
