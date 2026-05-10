package com.parque.dashboard.dto;

import java.math.BigDecimal;

public record TopHotelRevenueResponse(
        Long hotelId,
        String hotelName,
        BigDecimal revenue
) {
}

