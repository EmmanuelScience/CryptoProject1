package com.ticketswap.cryptoproject1.entities;

import lombok.Data;

import javax.persistence.*;

@Entity
@Data
@Table(name = "secret_key")
public class SecretKey {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    private Integer id;

    private String secretKey;

}