package com.parque.booking.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record TicketEntryValidationResponse(
        Long ticketId,
        Long bookingId,
        String holderFullName,
        String ticketStatus,
        LocalDate visitDate,
        LocalDateTime usedAt
) {
}
