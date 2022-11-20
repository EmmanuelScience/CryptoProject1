package com.ticketswap.cryptoproject1.repository;

import com.ticketswap.cryptoproject1.entities.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TicketRepository extends JpaRepository<Ticket, Integer> {

    @Query("SELECT t FROM Ticket t WHERE t.event.eventId = ?1")
    List<Ticket> findByEvent(Integer eventId);

    @Query("SELECT t FROM Ticket t WHERE t.ticketOwner.userId = ?1")
    List<Ticket> findByTicketOwner(Integer userId);
}
