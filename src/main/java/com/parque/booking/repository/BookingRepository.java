package com.parque.booking.repository;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import com.parque.booking.model.Booking;

import java.util.List;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    boolean existsByOfferId(Long offerId);

    @Override
    @EntityGraph(attributePaths = {"user", "hotel", "tickets"})
    Optional<Booking> findById(Long id);

    @EntityGraph(attributePaths = {"user", "hotel", "tickets"})
    List<Booking> findAllByOrderByCreatedAtDesc();
}
