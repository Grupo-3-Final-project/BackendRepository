package com.parque.dashboard.repository;

import com.parque.entity.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TicketRepository extends JpaRepository<Ticket, Long> {

    @Query("""
            select t.ageRange, count(t)
            from Ticket t
            where year(t.booking.createdAt) = :year
            group by t.ageRange
            """)
    List<Object[]> countTicketsByAgeRange(@Param("year") int year);
}

