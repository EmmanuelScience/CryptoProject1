package com.ticketswap.cryptoproject1.utils;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Date;
import java.util.Properties;

public class EmailUtility {
    private final String host;
    private final int port;
    private final String userName;
    private final String password;

    public EmailUtility() {
        host 	= "smtp.gmail.com";
        port	= 587;
        userName = "emmaonyeka4@gmail.com";
        password = "bqegwxldsnbudgqa";
    }

    public EmailUtility( String host, int port, String username, String password ) {
        this.host 	= host;
        this.port	= port;
        this.userName = username;
        this.password = password;
    }

    public  void sendMail(String subject, String toAddress, String message ) throws MessagingException {
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
        msg.setText(message);

        // sends the e-mail
        Transport t = session.getTransport("smtp");
        t.connect(userName, password);
        t.sendMessage(msg, msg.getAllRecipients());
        t.close();
    }
}
