package com.parque.booking.service.booking;

import com.parque.booking.dto.BookingCreateRequest;
import com.parque.booking.dto.BookingResponse;
import com.parque.booking.dto.BookingSummaryResponse;
import com.parque.booking.dto.CompanionRequest;
import com.parque.booking.dto.TicketResponse;
import com.parque.booking.model.Booking;
import com.parque.booking.model.TicketStatus;
import com.parque.booking.repository.BookingRepository;
import com.parque.booking.service.notification.NotificationService;
import com.parque.entity.Ticket;
import com.parque.exception.ConflictException;
import com.parque.exception.ResourceNotFoundException;
import com.parque.hotel.model.Hotel;
import com.parque.hotel.repository.HotelRepository;
import com.parque.offer.model.Offer;
import com.parque.offer.repository.OfferRepository;
import com.parque.user.model.User;
import com.parque.user.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
public class BookingServiceImpl implements BookingService {

    private static final int ADULT_AGE = 18;
    private static final int SENIOR_AGE = 65;

    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final HotelRepository hotelRepository;
    private final OfferRepository offerRepository;
    private final Optional<NotificationService> notificationService;

    public BookingServiceImpl(
            BookingRepository bookingRepository,
            UserRepository userRepository,
            HotelRepository hotelRepository,
            OfferRepository offerRepository,
            Optional<NotificationService> notificationService
    ) {
        this.bookingRepository = bookingRepository;
        this.userRepository = userRepository;
        this.hotelRepository = hotelRepository;
        this.offerRepository = offerRepository;
        this.notificationService = notificationService;
    }

    @Override
    public BookingResponse create(BookingCreateRequest request) {
        User user = userRepository.findById(request.userId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        List<CompanionRequest> companions = request.companions();
        validateCompanions(companions, request.visitDate());

        Offer offer = resolveOffer(request.offerId());
        Hotel hotel = resolveHotel(request.hotelId(), offer);
        String boardType = normalizeBoardType(request.boardType());

        if (hotel != null) {
            validateHotelAvailability(hotel, companions.size());
        }

        Booking booking = buildBooking(user, offer, hotel, boardType, request.visitDate());
        List<Ticket> tickets = buildTickets(booking, companions, request.visitDate(), boardType);
        booking.setTickets(tickets);
        booking.setTotalPrice(calculateTotalPrice(tickets, hotel, boardType));

        if (hotel != null) {
            reserveHotelPlaces(hotel, companions.size());
        }

        Booking saved = bookingRepository.saveAndFlush(booking);
        saved.setEmailSent(sendBookingConfirmation(user, saved));
        return toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookingSummaryResponse> getAll() {
        return bookingRepository.findAllByOrderByCreatedAtDesc().stream()
                .map(this::toSummary)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public BookingResponse getById(Long id) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found"));
        return toResponse(booking);
    }

    private void validateCompanions(List<CompanionRequest> companions, LocalDate visitDate) {
        if (companions == null || companions.isEmpty() || visitDate == null) {
            throw new IllegalArgumentException("Invalid booking data");
        }

        if (hasMinorWithoutAdult(companions, visitDate)) {
            throw new ConflictException("A minor cannot travel without an adult");
        }
    }

    private boolean hasMinorWithoutAdult(List<CompanionRequest> companions, LocalDate visitDate) {
        boolean hasAdult = companions.stream()
                .anyMatch(companion -> ageAtVisit(companion.birthDate(), visitDate) >= ADULT_AGE);

        boolean hasMinor = companions.stream()
                .anyMatch(companion -> ageAtVisit(companion.birthDate(), visitDate) < ADULT_AGE);

        return hasMinor && !hasAdult;
    }

    private Offer resolveOffer(Long offerId) {
        if (offerId == null) {
            return null;
        }
        return offerRepository.findById(offerId)
                .orElseThrow(() -> new ResourceNotFoundException("Offer not found"));
    }

    private Hotel resolveHotel(Long hotelId, Offer offer) {
        if (hotelId != null) {
            return hotelRepository.findById(hotelId)
                    .orElseThrow(() -> new ResourceNotFoundException("Hotel not found"));
        }
        if (offer != null) {
            return offer.getHotel();
        }
        return null;
    }

    private void validateHotelAvailability(Hotel hotel, int requiredPlaces) {
        if (hotel.getAvailablePlaces() == null || hotel.getAvailablePlaces() < requiredPlaces) {
            throw new ConflictException("Hotel is full");
        }
    }

    private Booking buildBooking(User user, Offer offer, Hotel hotel, String boardType, LocalDate visitDate) {
        return Booking.builder()
                .user(user)
                .offer(offer)
                .hotel(hotel)
                .boardType(boardType)
                .visitDate(visitDate)
                .emailSent(Boolean.FALSE)
                .build();
    }

    private List<Ticket> buildTickets(
            Booking booking,
            List<CompanionRequest> companions,
            LocalDate visitDate,
            String boardType
    ) {
        return companions.stream()
                .map(companion -> toTicket(booking, companion, visitDate, boardType))
                .toList();
    }

    private void reserveHotelPlaces(Hotel hotel, int requiredPlaces) {
        hotel.setAvailablePlaces(hotel.getAvailablePlaces() - requiredPlaces);
    }

    private Ticket toTicket(Booking booking, CompanionRequest companion, LocalDate visitDate, String boardType) {
        String ageRange = resolveAgeRange(companion.birthDate(), visitDate);
        return Ticket.builder()
                .booking(booking)
                .holderFullName(companion.firstName() + " " + companion.lastName())
                .ageRange(ageRange)
                .price(resolveTicketPrice(ageRange, boardType))
                .entryToken(generateToken())
                .mobileAccessToken(generateToken())
                .status(TicketStatus.VALID)
                .build();
    }

    private String resolveAgeRange(LocalDate birthDate, LocalDate visitDate) {
        int age = ageAtVisit(birthDate, visitDate);
        if (age < ADULT_AGE) {
            return "CHILD";
        }
        if (age >= SENIOR_AGE) {
            return "SENIOR";
        }
        return "ADULT";
    }

    private int ageAtVisit(LocalDate birthDate, LocalDate visitDate) {
        if (birthDate == null || visitDate == null || birthDate.isAfter(visitDate)) {
            throw new IllegalArgumentException("Invalid booking data");
        }
        return Period.between(birthDate, visitDate).getYears();
    }

    private BigDecimal resolveTicketPrice(String ageRange, String boardType) {
        return switch (ageRange) {
            case "CHILD" -> boardType.equals("HALF_BOARD")
                    ? new BigDecimal("20.00")
                    : new BigDecimal("30.00");
            case "SENIOR" -> boardType.equals("HALF_BOARD")
                    ? new BigDecimal("40.00")
                    : new BigDecimal("55.00");
            default -> boardType.equals("HALF_BOARD")
                    ? new BigDecimal("45.00")
                    : new BigDecimal("65.00");
        };
    }

    private BigDecimal calculateTotalPrice(List<Ticket> tickets, Hotel hotel, String boardType) {
        BigDecimal ticketsPrice = tickets.stream()
                .map(Ticket::getPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        if (hotel == null) {
            return ticketsPrice;
        }

        return ticketsPrice.add(resolveHotelPrice(hotel, boardType));
    }

    private BigDecimal resolveHotelPrice(Hotel hotel, String boardType) {
        return switch (boardType) {
            case "HALF_BOARD" -> hotel.getHalfBoardPrice();
            case "FULL_BOARD" -> hotel.getFullBoardPrice();
            default -> throw new IllegalArgumentException("Invalid booking data");
        };
    }

    private String normalizeBoardType(String boardType) {
        if (boardType == null) {
            throw new IllegalArgumentException("Invalid booking data");
        }
        String normalized = boardType.trim().toUpperCase();
        if (!normalized.equals("HALF_BOARD") && !normalized.equals("FULL_BOARD")) {
            throw new IllegalArgumentException("Invalid booking data");
        }
        return normalized;
    }

    private BookingSummaryResponse toSummary(Booking booking) {
        return new BookingSummaryResponse(
                booking.getId(),
                fullName(booking.getUser().getFirstName(), booking.getUser().getLastName()),
                booking.getHotel() == null ? null : booking.getHotel().getName(),
                booking.getVisitDate(),
                booking.getTickets() == null ? 0 : booking.getTickets().size(),
                booking.getTotalPrice(),
                normalizeCreatedAt(booking.getCreatedAt())
        );
    }

    private BookingResponse toResponse(Booking booking) {
        return new BookingResponse(
                booking.getId(),
                booking.getUser().getId(),
                fullName(booking.getUser().getFirstName(), booking.getUser().getLastName()),
                booking.getHotel() == null ? null : booking.getHotel().getId(),
                booking.getHotel() == null ? null : booking.getHotel().getName(),
                booking.getBoardType(),
                booking.getVisitDate(),
                toTicketResponses(booking.getTickets()),
                booking.getTotalPrice(),
                booking.getEmailSent(),
                normalizeCreatedAt(booking.getCreatedAt())
        );
    }

    private List<TicketResponse> toTicketResponses(List<Ticket> tickets) {
        if (tickets == null) {
            return List.of();
        }

        return tickets.stream()
                .map(ticket -> new TicketResponse(
                        ticket.getHolderFullName(),
                        ticket.getAgeRange(),
                        ticket.getPrice()))
                .toList();
    }

    private LocalDateTime normalizeCreatedAt(LocalDateTime createdAt) {
        return createdAt == null ? null : createdAt.withNano(0);
    }

    private String fullName(String firstName, String lastName) {
        return firstName + " " + lastName;
    }

    private boolean sendBookingConfirmation(User user, Booking booking) {
        if (user == null || user.getEmail() == null || user.getEmail().isBlank()) {
            return false;
        }

        return notificationService
                .map(service -> service.sendBookingConfirmation(List.of(user.getEmail()), booking))
                .orElse(false);
    }

    private String generateToken() {
        return UUID.randomUUID().toString().replace("-", "");
    }
}
