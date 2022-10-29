package com.ticketswap.cryptoproject1;

import com.ticketswap.cryptoproject1.entities.BankCard;
import com.ticketswap.cryptoproject1.entities.UserType;
import com.ticketswap.cryptoproject1.entities.Users;
import com.ticketswap.cryptoproject1.repository.BankCardRepository;
import com.ticketswap.cryptoproject1.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.security.NoSuchAlgorithmException;
import java.time.LocalDate;
import java.util.List;

import static com.ticketswap.cryptoproject1.CryptoProject1Application.hashPassword;

@SpringBootTest
class CryptoProject1ApplicationTests {


    @Autowired
    private UserRepository userRepository;
    @Autowired
    private BankCardRepository bankCardRepository;




    @Test
    void contextLoads() {
    }

    @Test
    void insertData() throws NoSuchAlgorithmException {
        Users user = new Users();
        user.setUserName("User1");
        user.setEmail("user1@gmail.com");
        user.setPhone(612415252);
        user.setUserType(UserType.Seller);
        String password = "1234";
        String hashedPassword = hashPassword(password);
        user.setPassword(hashedPassword);
        userRepository.save(user);

        BankCard bankCard = new BankCard();
        bankCard.setExpirationDate(LocalDate.now());
        bankCard.setCardNumber(1234567890);
        bankCard.setCardHolder("My freaking Card");
        bankCard.setCvv(123);
        bankCard.setUser(user);
        bankCardRepository.save(bankCard);
    }

    @Test
    void retrieveData() throws NoSuchAlgorithmException {
        String password = "1234";
        String email = "user1@gmail.com";
        List<Users> users = userRepository.findByEmail(email, hashPassword(password));
        System.out.println(users);
    }

}
