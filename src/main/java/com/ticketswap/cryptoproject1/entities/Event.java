package com.ticketswap.cryptoproject1.entities;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDate;
@Entity
@Data
@NoArgsConstructor
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "eventId", nullable = false)
    private Integer eventId;

    private String eventName;

    private String city;

    private String venue;

    private LocalDate date;

    public String toString() {
        return "event id: " + getEventId() +
                ", event name: " + getEventName() +
                ", event date: " + getDate() +
                ", event location: " + getEventName() +
                ", event venue: " + getVenue();
    }
}
