package com.parque.booking;

import com.parque.attraction.model.Attraction;
import com.parque.attraction.repository.AttractionRepository;
import com.parque.booking.dto.MobileAccessResponse;
import com.parque.booking.dto.TicketEntryValidationResponse;
import com.parque.booking.model.Booking;
import com.parque.booking.model.TicketStatus;
import com.parque.booking.repository.TicketAccessRepository;
import com.parque.booking.service.ticket.TicketAccessServiceImpl;
import com.parque.entity.Ticket;
import com.parque.exception.ConflictException;
import com.parque.exception.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Sort;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TicketAccessServiceImplTest {

    @Mock
    private TicketAccessRepository ticketAccessRepository;

    @Mock
    private AttractionRepository attractionRepository;

    private TicketAccessServiceImpl service;

    @BeforeEach
    void setUp() {
        service = new TicketAccessServiceImpl(ticketAccessRepository, attractionRepository);
    }

    @Test
    void getMobileAccess_shouldReturnMappedResponseAndSortedAttractions() {
        Ticket ticket = buildTicket("mobile-token", "entry-token", TicketStatus.VALID, LocalDate.now().plusDays(1));
        Attraction attraction = Attraction.builder()
                .id(5L)
                .name("Dragon Coaster")
                .description("Montana rusa principal")
                .size("LARGE")
                .status("OPEN")
                .totalSeats(24)
                .availableSeats(20)
                .maintenanceFrequencyDays(7)
                .imageUrl("https://example.com/dragon.jpg")
                .build();

        when(ticketAccessRepository.findByMobileAccessToken("mobile-token")).thenReturn(Optional.of(ticket));
        when(attractionRepository.findAll(any(Sort.class))).thenReturn(List.of(attraction));

        MobileAccessResponse response = service.getMobileAccess("mobile-token");

        assertThat(response.ticketId()).isEqualTo(ticket.getId());
        assertThat(response.bookingId()).isEqualTo(ticket.getBooking().getId());
        assertThat(response.holderFullName()).isEqualTo("Ana Garcia");
        assertThat(response.ticketStatus()).isEqualTo("VALID");
        assertThat(response.visitDate()).isEqualTo(ticket.getBooking().getVisitDate());
        assertThat(response.attractions()).singleElement().satisfies(mapped -> {
            assertThat(mapped.id()).isEqualTo(5L);
            assertThat(mapped.name()).isEqualTo("Dragon Coaster");
            assertThat(mapped.imageUrl()).isEqualTo("https://example.com/dragon.jpg");
        });

        verify(attractionRepository).findAll(Sort.by(Sort.Direction.ASC, "name"));
    }

    @Test
    void getMobileAccess_shouldThrowNotFoundWhenTicketDoesNotExist() {
        when(ticketAccessRepository.findByMobileAccessToken("missing-token")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.getMobileAccess("missing-token"))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Ticket not found");

        verifyNoInteractions(attractionRepository);
    }

    @Test
    void getMobileAccess_shouldRejectCancelledTickets() {
        when(ticketAccessRepository.findByMobileAccessToken("cancelled-token"))
                .thenReturn(Optional.of(buildTicket("cancelled-token", "entry-token", TicketStatus.CANCELLED, LocalDate.now().plusDays(1))));

        assertThatThrownBy(() -> service.getMobileAccess("cancelled-token"))
                .isInstanceOf(ConflictException.class)
                .hasMessage("Ticket is not available");

        verifyNoInteractions(attractionRepository);
    }

    @Test
    void validateEntry_shouldMarkTicketAsUsed() {
        Ticket ticket = buildTicket("mobile-token", "entry-token", TicketStatus.VALID, LocalDate.now());
        when(ticketAccessRepository.findByEntryToken("entry-token")).thenReturn(Optional.of(ticket));

        TicketEntryValidationResponse response = service.validateEntry("entry-token");

        assertThat(response.ticketId()).isEqualTo(ticket.getId());
        assertThat(response.ticketStatus()).isEqualTo("USED");
        assertThat(response.usedAt()).isNotNull();
        assertThat(ticket.getStatus()).isEqualTo(TicketStatus.USED);
        assertThat(ticket.getUsedAt()).isNotNull();
        assertThat(ticket.getUsedAt().getNano()).isZero();
    }

    @Test
    void validateEntry_shouldThrowNotFoundWhenTicketDoesNotExist() {
        when(ticketAccessRepository.findByEntryToken("missing-token")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.validateEntry("missing-token"))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Ticket not found");
    }

    @Test
    void validateEntry_shouldRejectCancelledTickets() {
        when(ticketAccessRepository.findByEntryToken("entry-token"))
                .thenReturn(Optional.of(buildTicket("mobile-token", "entry-token", TicketStatus.CANCELLED, LocalDate.now())));

        assertThatThrownBy(() -> service.validateEntry("entry-token"))
                .isInstanceOf(ConflictException.class)
                .hasMessage("Ticket is not available");
    }

    @Test
    void validateEntry_shouldRejectUsedTickets() {
        when(ticketAccessRepository.findByEntryToken("entry-token"))
                .thenReturn(Optional.of(buildTicket("mobile-token", "entry-token", TicketStatus.USED, LocalDate.now())));

        assertThatThrownBy(() -> service.validateEntry("entry-token"))
                .isInstanceOf(ConflictException.class)
                .hasMessage("Ticket already used");
    }

    @Test
    void validateEntry_shouldRejectTicketsForAnotherDate() {
        when(ticketAccessRepository.findByEntryToken("entry-token"))
                .thenReturn(Optional.of(buildTicket("mobile-token", "entry-token", TicketStatus.VALID, LocalDate.now().plusDays(1))));

        assertThatThrownBy(() -> service.validateEntry("entry-token"))
                .isInstanceOf(ConflictException.class)
                .hasMessage("Ticket is not valid for today");
    }

    private Ticket buildTicket(String mobileToken, String entryToken, TicketStatus status, LocalDate visitDate) {
        Booking booking = Booking.builder()
                .id(12L)
                .visitDate(visitDate)
                .boardType("FULL_BOARD")
                .totalPrice(new BigDecimal("65.00"))
                .emailSent(Boolean.TRUE)
                .build();

        return Ticket.builder()
                .id(3L)
                .booking(booking)
                .holderFullName("Ana Garcia")
                .ageRange("ADULT")
                .price(new BigDecimal("65.00"))
                .entryToken(entryToken)
                .mobileAccessToken(mobileToken)
                .status(status)
                .build();
    }
}
