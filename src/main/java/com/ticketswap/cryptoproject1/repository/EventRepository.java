package com.ticketswap.cryptoproject1.repository;

import com.ticketswap.cryptoproject1.entities.Event;
import com.ticketswap.cryptoproject1.entities.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EventRepository extends JpaRepository<Event, Integer> {

    @Query("SELECT t FROM Event t WHERE t.venue = ?1")
    List<Users> findByVenue(String venue);

    @Query("SELECT t FROM Event t WHERE t.city = ?1 ")
    List<Users> findByCity(String venue);

    @Query("SELECT t FROM Event t WHERE t.eventName = ?1")
    List<Users> findByEventName(String venue);

}
