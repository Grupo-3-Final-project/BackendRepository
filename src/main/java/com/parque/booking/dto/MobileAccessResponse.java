package com.parque.booking.dto;

import com.parque.attraction.dto.AttractionResponse;

import java.time.LocalDate;
import java.util.List;

public record MobileAccessResponse(
        Long ticketId,
        Long bookingId,
        String holderFullName,
        String ticketStatus,
        LocalDate visitDate,
        List<AttractionResponse> attractions
) {
}
