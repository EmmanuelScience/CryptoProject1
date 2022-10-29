package com.ticketswap.cryptoproject1.repository;

import com.ticketswap.cryptoproject1.entities.BankCard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BankCardRepository extends JpaRepository<BankCard, Integer> {
}
