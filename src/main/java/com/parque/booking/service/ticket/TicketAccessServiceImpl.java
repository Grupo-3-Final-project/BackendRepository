package com.parque.booking.service.ticket;

import com.parque.attraction.dto.AttractionResponse;
import com.parque.attraction.model.Attraction;
import com.parque.attraction.repository.AttractionRepository;
import com.parque.booking.dto.MobileAccessResponse;
import com.parque.booking.dto.TicketEntryValidationResponse;
import com.parque.booking.model.TicketStatus;
import com.parque.booking.repository.TicketAccessRepository;
import com.parque.entity.Ticket;
import com.parque.exception.ConflictException;
import com.parque.exception.ResourceNotFoundException;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class TicketAccessServiceImpl implements TicketAccessService {

    private final TicketAccessRepository ticketAccessRepository;
    private final AttractionRepository attractionRepository;

    public TicketAccessServiceImpl(
            TicketAccessRepository ticketAccessRepository,
            AttractionRepository attractionRepository
    ) {
        this.ticketAccessRepository = ticketAccessRepository;
        this.attractionRepository = attractionRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public MobileAccessResponse getMobileAccess(String mobileAccessToken) {
        Ticket ticket = ticketAccessRepository.findByMobileAccessToken(mobileAccessToken)
                .orElseThrow(() -> new ResourceNotFoundException("Ticket not found"));

        if (ticket.getStatus() == TicketStatus.CANCELLED) {
            throw new ConflictException("Ticket is not available");
        }

        List<AttractionResponse> attractions = attractionRepository.findAll(Sort.by(Sort.Direction.ASC, "name"))
                .stream()
                .map(this::toAttractionResponse)
                .toList();

        return new MobileAccessResponse(
                ticket.getId(),
                ticket.getBooking().getId(),
                ticket.getHolderFullName(),
                ticket.getStatus().name(),
                ticket.getBooking().getVisitDate(),
                attractions
        );
    }

    @Override
    public TicketEntryValidationResponse validateEntry(String entryToken) {
        Ticket ticket = ticketAccessRepository.findByEntryToken(entryToken)
                .orElseThrow(() -> new ResourceNotFoundException("Ticket not found"));

        if (ticket.getStatus() == TicketStatus.CANCELLED) {
            throw new ConflictException("Ticket is not available");
        }

        if (ticket.getStatus() == TicketStatus.USED) {
            throw new ConflictException("Ticket already used");
        }

        if (!LocalDate.now().equals(ticket.getBooking().getVisitDate())) {
            throw new ConflictException("Ticket is not valid for today");
        }

        ticket.setStatus(TicketStatus.USED);
        ticket.setUsedAt(LocalDateTime.now().withNano(0));

        return new TicketEntryValidationResponse(
                ticket.getId(),
                ticket.getBooking().getId(),
                ticket.getHolderFullName(),
                ticket.getStatus().name(),
                ticket.getBooking().getVisitDate(),
                ticket.getUsedAt()
        );
    }

    private AttractionResponse toAttractionResponse(Attraction attraction) {
        return new AttractionResponse(
                attraction.getId(),
                attraction.getName(),
                attraction.getDescription(),
                attraction.getSize(),
                attraction.getStatus(),
                attraction.getTotalSeats(),
                attraction.getAvailableSeats(),
                attraction.getMaintenanceFrequencyDays(),
                attraction.getImageUrl()
        );
    }
}
