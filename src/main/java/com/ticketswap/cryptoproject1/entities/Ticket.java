package com.ticketswap.cryptoproject1.entities;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Data
@NoArgsConstructor
public class Ticket {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "ticketId", nullable = false)
    private int ticketId;

    private String ticketCode;

    @ManyToOne
    @JoinColumn(name = "Event")
    private Event event;

    @ManyToOne
    @JoinColumn(name = "userId")
    private Users ticketOwner;

    private double ticketPrice;


}
