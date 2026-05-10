package com.parque.dashboard.service;

import com.parque.dashboard.dto.CurrentYearRevenueResponse;
import com.parque.dashboard.dto.DashboardSummaryResponse;
import com.parque.dashboard.dto.TicketsByAgeRangeResponse;
import com.parque.dashboard.dto.TopHotelRevenueResponse;
import com.parque.dashboard.repository.BookingDashboardRepository;
import com.parque.dashboard.repository.TicketRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

@Service
@Transactional(readOnly = true)
public class DashboardServiceImpl implements DashboardService {

    private static final Map<String, Integer> AGE_RANGE_ORDER = Map.of(
            "CHILD", 0,
            "ADULT", 1,
            "SENIOR", 2
    );

    private final TicketRepository ticketRepository;
    private final BookingDashboardRepository bookingDashboardRepository;

    public DashboardServiceImpl(TicketRepository ticketRepository, BookingDashboardRepository bookingDashboardRepository) {
        this.ticketRepository = ticketRepository;
        this.bookingDashboardRepository = bookingDashboardRepository;
    }

    @Override
    public List<TicketsByAgeRangeResponse> getTicketsByAgeRange(int year) {
        List<Object[]> rows = ticketRepository.countTicketsByAgeRange(year);
        return rows.stream()
                .map(r -> new TicketsByAgeRangeResponse((String) r[0], (Long) r[1]))
                .sorted(Comparator.comparing(response -> AGE_RANGE_ORDER.getOrDefault(response.ageRange(), Integer.MAX_VALUE)))
                .toList();
    }

    @Override
    public CurrentYearRevenueResponse getCurrentYearRevenue() {
        int year = LocalDate.now().getYear();
        BigDecimal revenue = bookingDashboardRepository.totalRevenueByYear(year);
        return new CurrentYearRevenueResponse(year, revenue);
    }

    @Override
    public List<TopHotelRevenueResponse> getTopHotels(int year) {
        return bookingDashboardRepository.topHotelsByRevenue(year).stream()
                .limit(3)
                .map(r -> new TopHotelRevenueResponse((Long) r[0], (String) r[1], (BigDecimal) r[2]))
                .toList();
    }

    @Override
    public DashboardSummaryResponse getSummary(int year) {
        BigDecimal revenue = bookingDashboardRepository.totalRevenueByYear(year);
        List<TicketsByAgeRangeResponse> tickets = getTicketsByAgeRange(year);
        List<TopHotelRevenueResponse> topHotels = getTopHotels(year);
        return new DashboardSummaryResponse(year, revenue, tickets, topHotels);
    }
}

