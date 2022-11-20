package com.ticketswap.cryptoproject1.repository;

import com.ticketswap.cryptoproject1.entities.BankCard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BankCardRepository extends JpaRepository<BankCard, Integer> {
    @Query("SELECT b FROM BankCard b WHERE b.user.userId = ?1")
    List<BankCard> findByUser(int userId);
}
