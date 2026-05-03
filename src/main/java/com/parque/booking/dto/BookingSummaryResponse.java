package com.parque.booking.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record BookingSummaryResponse(
        Long id,
        String userFullName,
        String hotelName,
        LocalDate visitDate,
        int totalTickets,
        BigDecimal totalPrice,
        LocalDateTime createdAt
) {
}
