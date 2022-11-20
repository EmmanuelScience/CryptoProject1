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
    @JoinColumn(name = "event_id")
    private Event event;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private Users ticketOwner;

    private double ticketPrice;

    private int quantity;


}
