package com.parque.dashboard.service;

import com.parque.dashboard.dto.CurrentYearRevenueResponse;
import com.parque.dashboard.dto.DashboardSummaryResponse;
import com.parque.dashboard.dto.TicketsByAgeRangeResponse;
import com.parque.dashboard.dto.TopHotelRevenueResponse;

import java.util.List;

public interface DashboardService {
    List<TicketsByAgeRangeResponse> getTicketsByAgeRange(int year);

    CurrentYearRevenueResponse getCurrentYearRevenue();

    List<TopHotelRevenueResponse> getTopHotels(int year);

    DashboardSummaryResponse getSummary(int year);
}

