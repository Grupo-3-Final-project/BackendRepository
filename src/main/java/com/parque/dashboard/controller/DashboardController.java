package com.parque.dashboard.controller;

import com.parque.dashboard.dto.CurrentYearRevenueResponse;
import com.parque.dashboard.dto.DashboardSummaryResponse;
import com.parque.dashboard.dto.TicketsByAgeRangeResponse;
import com.parque.dashboard.dto.TopHotelRevenueResponse;
import com.parque.dashboard.service.DashboardService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    private final DashboardService dashboardService;

    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping("/tickets-by-age-range")
    public List<TicketsByAgeRangeResponse> getTicketsByAgeRange(@RequestParam int year) {
        return dashboardService.getTicketsByAgeRange(year);
    }

    @GetMapping("/current-year-revenue")
    public CurrentYearRevenueResponse getCurrentYearRevenue() {
        return dashboardService.getCurrentYearRevenue();
    }

    @GetMapping("/top-hotels")
    public List<TopHotelRevenueResponse> getTopHotels(@RequestParam int year) {
        return dashboardService.getTopHotels(year);
    }

    @GetMapping("/summary")
    public DashboardSummaryResponse getSummary(@RequestParam int year) {
        return dashboardService.getSummary(year);
    }
}

