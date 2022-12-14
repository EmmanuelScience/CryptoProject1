package com.ticketswap.cryptoproject1.repository;

import com.ticketswap.cryptoproject1.entities.TicketRequest;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TicketRequestRepository extends CrudRepository<TicketRequest, String>{
    List<TicketRequest> findAll();
    List<TicketRequest> findByEmailSeller(String emailSeller);
}
