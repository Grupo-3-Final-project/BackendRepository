package com.parque.dashboard.repository;

import com.parque.entity.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;

public interface BookingDashboardRepository extends JpaRepository<Booking, Long> {

    @Query("""
            select coalesce(sum(b.totalPrice), 0)
            from Booking b
            where year(b.createdAt) = :year
            """)
    BigDecimal totalRevenueByYear(@Param("year") int year);

    @Query("""
            select b.hotel.id, b.hotel.name, coalesce(sum(b.totalPrice), 0)
            from Booking b
            where b.hotel is not null and year(b.createdAt) = :year
            group by b.hotel.id, b.hotel.name
            order by sum(b.totalPrice) desc
            """)
    List<Object[]> topHotelsByRevenue(@Param("year") int year);
}

