package com.ticketswap.cryptoproject1.repository;

import com.ticketswap.cryptoproject1.entities.SecretKey;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SecretKeyRepository extends JpaRepository<SecretKey, Integer> {
}
