package com.parque.booking.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public record BookingResponse(
        Long id,
        Long userId,
        String userFullName,
        Long hotelId,
        String hotelName,
        String boardType,
        LocalDate visitDate,
        List<TicketResponse> tickets,
        BigDecimal totalPrice,
        Boolean emailSent,
        LocalDateTime createdAt
) {
}
