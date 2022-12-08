package com.ticketswap.cryptoproject1.entities;

import com.ticketswap.cryptoproject1.config.Encryptor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Data
@NoArgsConstructor
public class Users {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "userId", nullable = false)
    private int userId;

    private String userName;
    private String email;
    private String password;
    private UserType userType;
    private String firstName;
    private String lastName;
    private String address;
    private String city;
    private String country;
    private String postalCode;
    private String phoneNumber;
    private String salt;

    @Column(columnDefinition = "LONGTEXT")
    @Convert(converter = Encryptor.class)
    private String privateKey;
    @Column(columnDefinition = "LONGTEXT")
    private String publicKey;
    @Column(columnDefinition = "LONGTEXT")
    private String certificate;
}
