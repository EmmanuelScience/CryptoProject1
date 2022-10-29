package com.ticketswap.cryptoproject1;

import com.ticketswap.cryptoproject1.entities.BankCard;
import com.ticketswap.cryptoproject1.entities.Event;
import com.ticketswap.cryptoproject1.entities.UserType;
import com.ticketswap.cryptoproject1.entities.Users;
import com.ticketswap.cryptoproject1.repository.BankCardRepository;
import com.ticketswap.cryptoproject1.repository.EventRepository;
import com.ticketswap.cryptoproject1.repository.TicketRepository;
import com.ticketswap.cryptoproject1.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDate;
import java.util.List;

@SpringBootApplication
public class CryptoProject1Application {
    @Autowired
    private BankCardRepository bankCardRepository;
    @Autowired
    private static UserRepository userRepository;

    @Autowired
    private static TicketRepository ticketRepository;

    @Autowired
    private static EventRepository eventRepository;



    public static void main(String[] args) throws NoSuchAlgorithmException {
        SpringApplication.run(CryptoProject1Application.class, args);

        System.out.println("Hello World");
        loginUser();

    }

    public void mainMenu(){
        System.out.println(
                " 1. Search Events \n" +
                " 2. Register User \n" +
                " 3. Login User");
    }


    public void searchEvent() {
        String search = "Madrid";
        List<Event> events;
        //eventRepository.findAll(search);
    }

    public void sellTicket() {

    }

    public void buyTicket() {


    }

    public void addEvent() {
        Event event = new Event();
        event.setEventName("Event 1");
        event.setDate(LocalDate.now());
        event.setCity("Madrid");
        event.setVenue("O2");
        eventRepository.save(event);
    }

    public void removeEvent() {


    }

    public void registerUser() throws NoSuchAlgorithmException {
        Users user = new Users();
        user.setUserName("User1");
        user.setEmail("user1@gmail.com");
        user.setPhone(612415252);
        user.setUserType(UserType.Seller);
        String password = "1234";
        String hashedPassword = hashPassword(password);
        user.setPassword(hashedPassword);
        userRepository.save(user);
    }

    public void addBankCard(Users user) {
        BankCard bankCard = new BankCard();
        bankCard.setExpirationDate(LocalDate.now());
        bankCard.setCardNumber(1234567890);
        bankCard.setCardHolder("My freaking Card");
        bankCard.setCvv(123);
        bankCard.setUser(user);
        bankCardRepository.save(bankCard);
    }

    public static void loginUser() throws NoSuchAlgorithmException {
        String password = "1234";
        String email = "user1@gmail.com";
        List<Users> users = userRepository.findByEmail(email, hashPassword(password));
        System.out.println(users);
    }

    public static String hashPassword(String password) throws NoSuchAlgorithmException {
        final MessageDigest digest = MessageDigest.getInstance("SHA3-256");
        final byte[] hashBytes = digest.digest(
                password.getBytes(StandardCharsets.UTF_8));
        return bytesToHex(hashBytes);
    }

    private static String bytesToHex(byte[] hash) {
        StringBuilder hexString = new StringBuilder(2 * hash.length);
        for (byte b : hash) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }
        return hexString.toString();
    }

}
