package com.parque.booking.repository;

import com.parque.entity.Ticket;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TicketAccessRepository extends JpaRepository<Ticket, Long> {

    @EntityGraph(attributePaths = {"booking"})
    Optional<Ticket> findByEntryToken(String entryToken);

    @EntityGraph(attributePaths = {"booking"})
    Optional<Ticket> findByMobileAccessToken(String mobileAccessToken);
}
