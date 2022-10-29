package com.ticketswap.cryptoproject1.repository;

import com.ticketswap.cryptoproject1.entities.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TicketRepository extends JpaRepository<Ticket, Integer> {
}
