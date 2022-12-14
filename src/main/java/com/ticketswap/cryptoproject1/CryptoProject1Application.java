package com.ticketswap.cryptoproject1;

import com.ticketswap.cryptoproject1.repository.TicketRequestRepository;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

import java.security.GeneralSecurityException;
@EnableMongoRepositories(basePackageClasses  = TicketRequestRepository.class)
@SpringBootApplication
public class CryptoProject1Application {
    public static void main(String[] args) throws GeneralSecurityException {
        SpringApplication.run(CryptoProject1Application.class, args);
        AppSession session = ApplicationContextHolder.getContext().getBean(AppSession.class);
        session.mainMenu();
    }
}
