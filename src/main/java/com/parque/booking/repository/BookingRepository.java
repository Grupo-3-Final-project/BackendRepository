package com.parque.booking.repository;

import com.parque.entity.Booking;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    @Override
    @EntityGraph(attributePaths = {"user", "hotel", "tickets"})
    Optional<Booking> findById(Long id);

    @EntityGraph(attributePaths = {"user", "hotel", "tickets"})
    List<Booking> findAllByOrderByCreatedAtDesc();
}
