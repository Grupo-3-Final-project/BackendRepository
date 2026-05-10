package com.parque.dashboard.dto;

import java.math.BigDecimal;
import java.util.List;

public record DashboardSummaryResponse(
        int year,
        BigDecimal totalRevenue,
        List<TicketsByAgeRangeResponse> ticketsByAgeRange,
        List<TopHotelRevenueResponse> topHotels
) {
}

