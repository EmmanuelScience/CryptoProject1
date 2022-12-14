package com.ticketswap.cryptoproject1.emailServer;

import com.ticketswap.cryptoproject1.crypto.RSA;
import com.ticketswap.cryptoproject1.utils.InputHelper;
import lombok.SneakyThrows;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.*;
import java.net.Socket;
import java.security.PrivateKey;
import java.util.*;

import static com.ticketswap.cryptoproject1.crypto.DigitalSignature.readKeyFromFile;

class ConnectionHandler implements Runnable {
    private final Socket socket;
    private final String userName;
    private final String password;
    private final String host;
    private final int port;

    RSA rsa = new RSA();

    // Constructor
    public ConnectionHandler(Socket socket) {
        this.socket = socket;
        host 	= "smtp.gmail.com";
        port	= 587;
        userName = "emmaonyeka4@gmail.com";
        password = InputHelper.getStringInput("Enter email password: ");
        String keyPassword = InputHelper.getStringInput("Enter key password: ");
        PrivateKey privateKey = readKeyFromFile("C:\\chomsky\\Academics\\Fall-2022\\crypto\\CryptoProject1\\src\\main\\java\\com\\ticketswap\\cryptoproject1\\emailServer\\encryptedPrivateKey.key", keyPassword);
        rsa.setPrivateKeyString(privateKey.toString());
    }

    @SneakyThrows
    @Override
    public void run() {
        try {
            // Create a BufferedReader to read from the socket
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(socket.getInputStream()));

            // Create a PrintWriter to write to the socket
            PrintWriter writer = new PrintWriter(
                    new OutputStreamWriter(socket.getOutputStream()));

            // Read the incoming message
            String line;
            while ((line = reader.readLine()) != null) {
                // Print the incoming message to the console
                String decryptedMessage = rsa.decrypt(line);
                System.out.println(decryptedMessage);
                sendMail(decryptedMessage);

                // If the incoming message is "QUIT", close the socket
                if (line.equalsIgnoreCase("QUIT")) {
                    socket.close();
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



    public  void sendMail(String message) throws MessagingException {
        //Parse the email message before sending
        List<String> parsedMessage = parseMessage(message);
        String subject = parsedMessage.get(0);
        String toAddress = parsedMessage.get(1);
        String body = parsedMessage.get(2);
        // Set Properties
        Properties props = new Properties();
        props.put("mail.smtp.ssl.protocols", "TLSv1.2");
        props.put( "mail.smtp.auth", "true" );
        props.put( "mail.smtp.host", host );
        props.put( "mail.smtp.port", port );
        props.put( "mail.smtp.starttls.enable", "true" );
        props.put("mail.smtp.user", userName);

        // creates a new session, no Authenticator (will connect() later)
        Session session = Session.getDefaultInstance(props);

        // creates a new e-mail message
        Message msg = new MimeMessage(session);

        msg.setFrom(new InternetAddress(userName));
        InternetAddress[] toAddresses = { new InternetAddress(toAddress) };
        msg.setRecipients(Message.RecipientType.TO, toAddresses);
        msg.setSubject(subject);
        msg.setSentDate(new Date());
        // set plain text message
        msg.setText(body);

        // sends the e-mail
        Transport t = session.getTransport("smtp");
        t.connect(userName, password);
        t.sendMessage(msg, msg.getAllRecipients());
        t.close();
    }

    public List<String> parseMessage(String message) {
        List<String> parsedMessage = new ArrayList<>();
        String[] splitMessage = message.split("%2B");
        Collections.addAll(parsedMessage, splitMessage);
        return parsedMessage;
    }
}




