package com.parque.booking.controller;

import com.parque.booking.dto.MobileAccessResponse;
import com.parque.booking.dto.TicketEntryValidationResponse;
import com.parque.booking.service.ticket.TicketAccessService;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/tickets")
@SecurityRequirements
public class TicketController {

    private final TicketAccessService ticketAccessService;

    public TicketController(TicketAccessService ticketAccessService) {
        this.ticketAccessService = ticketAccessService;
    }

    @GetMapping("/mobile/{mobileAccessToken}")
    public MobileAccessResponse getMobileAccess(@PathVariable String mobileAccessToken) {
        return ticketAccessService.getMobileAccess(mobileAccessToken);
    }

    @PostMapping("/entry/{entryToken}/validate")
    public TicketEntryValidationResponse validateEntry(@PathVariable String entryToken) {
        return ticketAccessService.validateEntry(entryToken);
    }
}
