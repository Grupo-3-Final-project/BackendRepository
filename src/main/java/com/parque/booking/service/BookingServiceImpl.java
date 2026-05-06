package com.parque.booking.service;

import com.parque.booking.dto.BookingCreateRequest;
import com.parque.booking.dto.BookingResponse;
import com.parque.booking.dto.BookingSummaryResponse;
import com.parque.booking.dto.CompanionRequest;
import com.parque.booking.dto.TicketResponse;
import com.parque.booking.model.Booking;
import com.parque.booking.repository.BookingRepository;
import com.parque.entity.Ticket;
import com.parque.enums.PaymentStatus;
import com.parque.exception.ConflictException;
import com.parque.exception.ResourceNotFoundException;
import com.parque.hotel.model.Hotel;
import com.parque.hotel.repository.HotelRepository;
import com.parque.offer.model.Offer;
import com.parque.offer.repository.OfferRepository;
import com.parque.user.model.User;
import com.parque.user.repository.UserRepository;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
@Transactional
public class BookingServiceImpl implements BookingService {

    private static final int ADULT_AGE = 18;
    private static final int SENIOR_AGE = 65;
    private static final BigDecimal CHILD_PRICE = new BigDecimal("25.00");
    private static final BigDecimal ADULT_PRICE = new BigDecimal("45.00");
    private static final BigDecimal SENIOR_PRICE = new BigDecimal("30.00");

    private final JavaMailSender mailSender;
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final HotelRepository hotelRepository;
    private final OfferRepository offerRepository;

    private static final Logger log = LoggerFactory.getLogger(BookingServiceImpl.class);

    public BookingServiceImpl(
            BookingRepository bookingRepository,
            UserRepository userRepository,
            HotelRepository hotelRepository,
            OfferRepository offerRepository) {
        this.mailSender = null;
        this.bookingRepository = bookingRepository;
        this.userRepository = userRepository;
        this.hotelRepository = hotelRepository;
        this.offerRepository = offerRepository;
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

        Booking booking = Booking.builder()
                .user(user)
                .offer(offer)
                .hotel(hotel)
                .boardType(boardType)
                .visitDate(request.visitDate())
                .emailSent(Boolean.TRUE)
                .build();

        List<Ticket> tickets = companions.stream()
                .map(companion -> toTicket(booking, companion, request.visitDate()))
                .toList();

        booking.setTickets(tickets);
        booking.setTotalPrice(calculateTotalPrice(tickets, hotel, boardType));

        if (hotel != null) {
            hotel.setAvailablePlaces(hotel.getAvailablePlaces() - companions.size());
        }

        Booking saved = bookingRepository.saveAndFlush(booking);
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

        boolean hasAdult = companions.stream()
                .anyMatch(companion -> ageAtVisit(companion.birthDate(), visitDate) >= ADULT_AGE);

        boolean hasMinor = companions.stream()
                .anyMatch(companion -> ageAtVisit(companion.birthDate(), visitDate) < ADULT_AGE);

        if (hasMinor && !hasAdult) {
            throw new ConflictException("A minor cannot travel without an adult");
        }
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

    private Ticket toTicket(Booking booking, CompanionRequest companion, LocalDate visitDate) {
        String ageRange = resolveAgeRange(companion.birthDate(), visitDate);
        return Ticket.builder()
                .booking(booking)
                .holderFullName(companion.firstName() + " " + companion.lastName())
                .ageRange(ageRange)
                .price(resolveTicketPrice(ageRange))
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

    private BigDecimal resolveTicketPrice(String ageRange) {
        return switch (ageRange) {
            case "CHILD" -> CHILD_PRICE;
            case "SENIOR" -> SENIOR_PRICE;
            default -> ADULT_PRICE;
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
                normalizeCreatedAt(booking.getCreatedAt()));
    }

    private BookingResponse toResponse(Booking booking) {
        List<TicketResponse> tickets = booking.getTickets() == null
                ? List.of()
                : booking.getTickets().stream()
                        .map(ticket -> new TicketResponse(
                                ticket.getHolderFullName(),
                                ticket.getAgeRange(),
                                ticket.getPrice()))
                        .toList();

        return new BookingResponse(
                booking.getId(),
                booking.getUser().getId(),
                fullName(booking.getUser().getFirstName(), booking.getUser().getLastName()),
                booking.getHotel() == null ? null : booking.getHotel().getId(),
                booking.getHotel() == null ? null : booking.getHotel().getName(),
                booking.getBoardType(),
                booking.getVisitDate(),
                tickets,
                booking.getTotalPrice(),
                booking.getEmailSent(),
                normalizeCreatedAt(booking.getCreatedAt()));
    }

    private LocalDateTime normalizeCreatedAt(LocalDateTime createdAt) {
        return createdAt == null ? null : createdAt.withNano(0);
    }

    private String fullName(String firstName, String lastName) {
        return firstName + " " + lastName;
    }

    @Override
    public PaymentStatus SetBookStatus(Booking booking, PaymentStatus status) {
        booking.getPayment().setStatus(status);

        bookingRepository.save(booking);

        return booking.getPayment().getStatus();
    }

    public ArrayList<String> AddParticipantsToBok(ArrayList<String> emailsParticipants, Booking book) {
        book.getEmailsParticipants().addAll(emailsParticipants);
        return book.getEmailsParticipants();
    }

    public boolean ChangeStatus(PaymentStatus status, Booking book) {
        if (status.equals(book.getPayment().getStatus())) {
            return false;
        }
        book.getPayment().setStatus(status);
        return true;
    }




   @Override
    public void sendEmails(Booking booking) {
        if (booking.getEmailsParticipants() == null || booking.getEmailsParticipants().isEmpty()) {
            log.warn("No hay destinatarios para la reserva {}", booking.getId());
            return;
        }

        booking.getEmailsParticipants().forEach(email -> sendEmail(email, booking));
    }

    private void sendEmail(String email, Booking booking) {
        try {
            SimpleMailMessage message = buildMessage(email, booking);
            mailSender.send(message);
            log.info("Email enviado a: {}", email);
        } catch (Exception e) {
            log.error("Error enviando email a {}", email, e);
            throw e;
        }
    }

    private SimpleMailMessage buildMessage(String email, Booking booking) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject("Confirmación de reserva - La Última Puerta");
        message.setText("""
                Tu reserva se ha realizado correctamente.

                Número de reserva: %s
                Fecha de visita: %s
                Hotel: %s
                Tipo de pensión: %s
                Precio total: %s €
                """.formatted(
                booking.getId(),
                booking.getVisitDate(),
                booking.getHotel() == null ? "Sin hotel" : booking.getHotel().getName(),
                booking.getBoardType() == null ? "Sin pensión" : booking.getBoardType(),
                booking.getTotalPrice()
        ));
        return message;
    }


}
