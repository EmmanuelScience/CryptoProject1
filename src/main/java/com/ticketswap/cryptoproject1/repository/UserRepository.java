package com.ticketswap.cryptoproject1.repository;

import com.ticketswap.cryptoproject1.entities.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<Users, Integer> {

    @Query("SELECT t FROM Users t WHERE t.email = ?1 AND t.password = ?2")
    List<Users> findByEmailPassword(String email, String password);

    @Query("SELECT t FROM Users t WHERE t.email = ?1")
    List<Users> findByEmail(String email);
}
