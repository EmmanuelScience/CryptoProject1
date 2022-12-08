package com.ticketswap.cryptoproject1.repository;
import java.util.List;

import com.ticketswap.cryptoproject1.entities.TicketRequest;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
public interface TicketRequestRepository extends CrudRepository<TicketRequest, String>{
    List<TicketRequest> findAll();
    List<TicketRequest> findByEmailSeller(String emailSeller);
}
