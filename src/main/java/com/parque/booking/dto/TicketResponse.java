package com.parque.booking.dto;

import java.math.BigDecimal;

public record TicketResponse(
        String holderFullName,
        String ageRange,
        BigDecimal price
) {
}
