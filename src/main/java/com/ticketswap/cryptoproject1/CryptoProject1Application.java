package com.ticketswap.cryptoproject1;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.security.GeneralSecurityException;

@SpringBootApplication

public class CryptoProject1Application {
    public static void main(String[] args) throws GeneralSecurityException {
        SpringApplication.run(CryptoProject1Application.class, args);
        AppSession session = ApplicationContextHolder.getContext().getBean(AppSession.class);
        session.mainMenu();
    }
}
