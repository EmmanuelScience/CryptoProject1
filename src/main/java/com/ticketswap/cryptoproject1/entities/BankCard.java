package com.ticketswap.cryptoproject1.entities;

import com.ticketswap.cryptoproject1.config.Encryptor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Data
@NoArgsConstructor
public class BankCard {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "bankCardId", nullable = false)
    private int bankCardId;

    @Convert(converter = Encryptor.class)
    private String cardHolder;

    @Convert(converter = Encryptor.class)
    private Integer cvv;

    @Convert(converter = Encryptor.class)
    private Integer cardNumber;

    @Convert(converter = Encryptor.class)
    private LocalDate expirationDate;


    @ManyToOne
    @JoinColumn(name = "user_id")
    private Users user;

}
