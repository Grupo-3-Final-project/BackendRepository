package com.parque.booking;

import com.parque.booking.dto.BookingCreateRequest;
import com.parque.booking.dto.BookingSummaryResponse;
import com.parque.booking.dto.CompanionRequest;
import com.parque.booking.model.Booking;
import com.parque.booking.repository.BookingRepository;
import com.parque.booking.service.booking.BookingServiceImpl;
import com.parque.booking.service.notification.NotificationService;
import com.parque.exception.ResourceNotFoundException;
import com.parque.hotel.model.Hotel;
import com.parque.hotel.repository.HotelRepository;
import com.parque.offer.model.Offer;
import com.parque.offer.repository.OfferRepository;
import com.parque.user.model.User;
import com.parque.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BookingServiceImplUnitTest {

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private HotelRepository hotelRepository;

    @Mock
    private OfferRepository offerRepository;

    @Mock
    private NotificationService notificationService;

    private BookingServiceImpl bookingService;

    @BeforeEach
    void setUp() {
        bookingService = new BookingServiceImpl(
                bookingRepository,
                userRepository,
                hotelRepository,
                offerRepository,
                Optional.of(notificationService)
        );
    }

    @Test
    void create_shouldUseOfferHotelAndNormalizeBoardType() {
        stubSavedBooking();
        User user = buildUser(1L, "cliente@example.com");
        Hotel hotel = buildHotel(4);
        Offer offer = Offer.builder()
                .id(9L)
                .hotel(hotel)
                .boardType("FULL_BOARD")
                .includedTickets(2)
                .totalPrice(new BigDecimal("399.99"))
                .title("Pack familiar")
                .description("Oferta demo")
                .imageUrl("https://example.com/offer.jpg")
                .build();

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(offerRepository.findById(9L)).thenReturn(Optional.of(offer));
        when(notificationService.sendBookingConfirmation(anyList(), any(Booking.class))).thenReturn(true);

        var response = bookingService.create(new BookingCreateRequest(
                1L,
                9L,
                null,
                " half_board ",
                LocalDate.parse("2026-06-01"),
                List.of(
                        new CompanionRequest("Ana", "Garcia", LocalDate.parse("1988-03-10")),
                        new CompanionRequest("Lucas", "Garcia", LocalDate.parse("2015-07-20"))
                )
        ));

        assertThat(response.hotelId()).isEqualTo(hotel.getId());
        assertThat(response.hotelName()).isEqualTo("Hotel Eclipse");
        assertThat(response.boardType()).isEqualTo("HALF_BOARD");
        assertThat(response.totalPrice()).isEqualByComparingTo("145.00");
        assertThat(response.emailSent()).isTrue();
        assertThat(response.tickets()).hasSize(2);
        assertThat(response.tickets()).extracting(ticket -> ticket.ageRange())
                .containsExactlyInAnyOrder("ADULT", "CHILD");
        assertThat(hotel.getAvailablePlaces()).isEqualTo(2);

        verify(notificationService).sendBookingConfirmation(anyList(), any(Booking.class));
        verifyNoInteractions(hotelRepository);
    }

    @Test
    void create_shouldReturnEmailSentFalseWhenNotificationServiceIsMissing() {
        stubSavedBooking();
        BookingServiceImpl serviceWithoutNotifications = new BookingServiceImpl(
                bookingRepository,
                userRepository,
                hotelRepository,
                offerRepository,
                Optional.empty()
        );

        when(userRepository.findById(1L)).thenReturn(Optional.of(buildUser(1L, "cliente@example.com")));

        var response = serviceWithoutNotifications.create(new BookingCreateRequest(
                1L,
                null,
                null,
                "FULL_BOARD",
                LocalDate.parse("2026-06-01"),
                List.of(new CompanionRequest("Ana", "Garcia", LocalDate.parse("1988-03-10")))
        ));

        assertThat(response.hotelId()).isNull();
        assertThat(response.totalPrice()).isEqualByComparingTo("65.00");
        assertThat(response.emailSent()).isFalse();
        verifyNoInteractions(notificationService);
    }

    @Test
    void create_shouldReturnEmailSentFalseWhenUserEmailIsBlank() {
        stubSavedBooking();
        when(userRepository.findById(1L)).thenReturn(Optional.of(buildUser(1L, " ")));

        var response = bookingService.create(new BookingCreateRequest(
                1L,
                null,
                null,
                "HALF_BOARD",
                LocalDate.parse("2026-06-01"),
                List.of(new CompanionRequest("Ana", "Garcia", LocalDate.parse("1988-03-10")))
        ));

        assertThat(response.emailSent()).isFalse();
        assertThat(response.totalPrice()).isEqualByComparingTo("45.00");
        verifyNoInteractions(notificationService);
    }

    @Test
    void create_shouldThrowNotFoundWhenOfferDoesNotExist() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(buildUser(1L, "cliente@example.com")));
        when(offerRepository.findById(44L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> bookingService.create(new BookingCreateRequest(
                1L,
                44L,
                null,
                "FULL_BOARD",
                LocalDate.parse("2026-06-01"),
                List.of(new CompanionRequest("Ana", "Garcia", LocalDate.parse("1988-03-10")))
        )))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Offer not found");
    }

    @Test
    void create_shouldRejectInvalidBoardType() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(buildUser(1L, "cliente@example.com")));

        assertThatThrownBy(() -> bookingService.create(new BookingCreateRequest(
                1L,
                null,
                null,
                "vip",
                LocalDate.parse("2026-06-01"),
                List.of(new CompanionRequest("Ana", "Garcia", LocalDate.parse("1988-03-10")))
        )))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Invalid booking data");
    }

    @Test
    void getAll_shouldMapSummaryWhenTicketsAreMissing() {
        User user = buildUser(1L, "cliente@example.com");
        Booking booking = Booking.builder()
                .id(4L)
                .user(user)
                .hotel(null)
                .boardType("FULL_BOARD")
                .visitDate(LocalDate.parse("2026-06-01"))
                .tickets(null)
                .totalPrice(new BigDecimal("65.00"))
                .emailSent(Boolean.FALSE)
                .createdAt(LocalDateTime.of(2026, 5, 18, 10, 15, 30, 900_000_000))
                .build();

        when(bookingRepository.findAllByOrderByCreatedAtDesc()).thenReturn(List.of(booking));

        List<BookingSummaryResponse> summaries = bookingService.getAll();

        assertThat(summaries).singleElement().satisfies(summary -> {
            assertThat(summary.id()).isEqualTo(4L);
            assertThat(summary.userFullName()).isEqualTo("Ana Garcia");
            assertThat(summary.hotelName()).isNull();
            assertThat(summary.totalTickets()).isZero();
            assertThat(summary.totalPrice()).isEqualByComparingTo("65.00");
            assertThat(summary.createdAt()).isEqualTo(LocalDateTime.of(2026, 5, 18, 10, 15, 30));
        });
    }

    private User buildUser(Long id, String email) {
        return User.builder()
                .id(id)
                .firstName("Ana")
                .lastName("Garcia")
                .email(email)
                .phone("600123123")
                .dni("12345678A")
                .birthDate(LocalDate.parse("1990-01-10"))
                .build();
    }

    private Hotel buildHotel(int availablePlaces) {
        return Hotel.builder()
                .id(3L)
                .name("Hotel Eclipse")
                .description("Hotel demo")
                .totalRooms(80)
                .availableRooms(60)
                .totalPlaces(160)
                .availablePlaces(availablePlaces)
                .halfBoardPrice(new BigDecimal("80.00"))
                .fullBoardPrice(new BigDecimal("120.00"))
                .imageUrl("https://example.com/hotel.jpg")
                .build();
    }

    private void stubSavedBooking() {
        when(bookingRepository.saveAndFlush(any(Booking.class))).thenAnswer(invocation -> {
            Booking booking = invocation.getArgument(0);
            booking.setId(12L);
            booking.setCreatedAt(LocalDateTime.of(2026, 5, 18, 11, 30));
            return booking;
        });
    }
}
