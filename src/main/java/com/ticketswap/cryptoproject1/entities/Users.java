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

    private String password;

    private String userSalt;

    private String email;

    private int phone;

    private UserType userType;

}
