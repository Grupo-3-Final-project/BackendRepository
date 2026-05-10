package com.parque.dashboard.dto;

public record TicketsByAgeRangeResponse(
        String ageRange,
        long ticketsSold
) {
}

