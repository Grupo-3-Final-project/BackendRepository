package com.parque.dashboard;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.parque.dashboard.repository.BookingDashboardRepository;
import com.parque.dashboard.repository.TicketRepository;
import com.parque.entity.Booking;
import com.parque.entity.Hotel;
import com.parque.entity.Ticket;
import com.parque.entity.User;
import com.parque.hotel.repository.HotelRepository;
import com.parque.testconfig.JacksonTestConfig;
import com.parque.user.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.client.RestClient;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(JacksonTestConfig.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
class DashboardControllerIT {

    @LocalServerPort
    private int port;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private TicketRepository ticketRepository;

    @Autowired
    private BookingDashboardRepository bookingDashboardRepository;

    @Autowired
    private HotelRepository hotelRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    void endpoints_shouldReturn200WithEmptyMetricsWhenNoData() throws Exception {
        ResponseEntity<String> revenue = restClient()
                .get()
                .uri("/api/dashboard/current-year-revenue")
                .retrieve()
                .toEntity(String.class);

        assertThat(revenue.getStatusCode()).isEqualTo(HttpStatus.OK);
        JsonNode revenueBody = objectMapper.readTree(revenue.getBody());
        assertThat(revenueBody.get("year").isNumber()).isTrue();
        assertThat(revenueBody.get("totalRevenue").isNumber()).isTrue();

        ResponseEntity<String> tickets = restClient()
                .get()
                .uri("/api/dashboard/tickets-by-age-range?year=2026")
                .retrieve()
                .toEntity(String.class);
        assertThat(tickets.getStatusCode()).isEqualTo(HttpStatus.OK);
        JsonNode ticketsBody = objectMapper.readTree(tickets.getBody());
        assertThat(ticketsBody.isArray()).isTrue();

        ResponseEntity<String> topHotels = restClient()
                .get()
                .uri("/api/dashboard/top-hotels?year=2026")
                .retrieve()
                .toEntity(String.class);
        assertThat(topHotels.getStatusCode()).isEqualTo(HttpStatus.OK);
        JsonNode topHotelsBody = objectMapper.readTree(topHotels.getBody());
        assertThat(topHotelsBody.isArray()).isTrue();

        ResponseEntity<String> summary = restClient()
                .get()
                .uri("/api/dashboard/summary?year=2026")
                .retrieve()
                .toEntity(String.class);
        assertThat(summary.getStatusCode()).isEqualTo(HttpStatus.OK);
        JsonNode summaryBody = objectMapper.readTree(summary.getBody());
        assertThat(summaryBody.get("year").asInt()).isEqualTo(2026);
    }

    @Test
    void endpoints_shouldReturnRealMetricsFromSeededBookings() throws Exception {
        ticketRepository.deleteAll();
        bookingDashboardRepository.deleteAll();
        hotelRepository.deleteAll();
        userRepository.deleteAll();

        User user = userRepository.save(User.builder()
                .firstName("David")
                .lastName("Navarro")
                .dni("12345678A")
                .email("david@example.com")
                .phone("600123123")
                .birthDate(LocalDate.parse("1990-04-15"))
                .build());

        Hotel hotelA = hotelRepository.save(Hotel.builder()
                .name("Hotel Magic Park")
                .description("Hotel familiar situado junto al parque.")
                .totalRooms(120)
                .availableRooms(120)
                .totalPlaces(240)
                .availablePlaces(240)
                .halfBoardPrice(new BigDecimal("80.0"))
                .fullBoardPrice(new BigDecimal("120.0"))
                .imageUrl("https://example.com/hotel.jpg")
                .build());

        Hotel hotelB = hotelRepository.save(Hotel.builder()
                .name("Hotel Adventure")
                .description("Hotel junto al parque.")
                .totalRooms(100)
                .availableRooms(100)
                .totalPlaces(200)
                .availablePlaces(200)
                .halfBoardPrice(new BigDecimal("70.0"))
                .fullBoardPrice(new BigDecimal("110.0"))
                .imageUrl("https://example.com/hotel2.jpg")
                .build());

        createBooking(user, hotelA, LocalDateTime.parse("2026-05-01T10:00:00"), new BigDecimal("300.00"), List.of(
                new TicketSeed("Ana Garcia", "ADULT", new BigDecimal("45.00")),
                new TicketSeed("Lucas Garcia", "CHILD", new BigDecimal("25.00")),
                new TicketSeed("Maria Perez", "SENIOR", new BigDecimal("30.00"))
        ));

        createBooking(user, hotelA, LocalDateTime.parse("2026-06-01T10:00:00"), new BigDecimal("200.00"), List.of(
                new TicketSeed("Pedro Lopez", "ADULT", new BigDecimal("45.00")),
                new TicketSeed("Laura Lopez", "ADULT", new BigDecimal("45.00"))
        ));

        createBooking(user, hotelB, LocalDateTime.parse("2026-07-01T10:00:00"), new BigDecimal("100.00"), List.of(
                new TicketSeed("Nina Ruiz", "CHILD", new BigDecimal("25.00"))
        ));

        createBooking(user, hotelB, LocalDateTime.parse("2025-07-01T10:00:00"), new BigDecimal("999.00"), List.of(
                new TicketSeed("Old Ticket", "ADULT", new BigDecimal("45.00"))
        ));

        ResponseEntity<String> tickets = restClient()
                .get()
                .uri("/api/dashboard/tickets-by-age-range?year=2026")
                .retrieve()
                .toEntity(String.class);
        assertThat(tickets.getStatusCode()).isEqualTo(HttpStatus.OK);
        JsonNode ticketsBody = objectMapper.readTree(tickets.getBody());
        assertThat(ticketsBody.isArray()).isTrue();

        long adultCount = 0;
        long childCount = 0;
        long seniorCount = 0;
        for (JsonNode row : ticketsBody) {
            String ageRange = row.get("ageRange").asText();
            long ticketsSold = row.get("ticketsSold").asLong();
            if ("ADULT".equals(ageRange)) {
                adultCount = ticketsSold;
            }
            if ("CHILD".equals(ageRange)) {
                childCount = ticketsSold;
            }
            if ("SENIOR".equals(ageRange)) {
                seniorCount = ticketsSold;
            }
        }
        assertThat(adultCount).isEqualTo(3);
        assertThat(childCount).isEqualTo(2);
        assertThat(seniorCount).isEqualTo(1);

        ResponseEntity<String> revenue = restClient()
                .get()
                .uri("/api/dashboard/current-year-revenue")
                .retrieve()
                .toEntity(String.class);
        assertThat(revenue.getStatusCode()).isEqualTo(HttpStatus.OK);
        JsonNode revenueBody = objectMapper.readTree(revenue.getBody());
        assertThat(revenueBody.get("year").asInt()).isEqualTo(2026);
        assertThat(revenueBody.get("totalRevenue").asDouble()).isEqualTo(600.0);

        ResponseEntity<String> topHotels = restClient()
                .get()
                .uri("/api/dashboard/top-hotels?year=2026")
                .retrieve()
                .toEntity(String.class);
        assertThat(topHotels.getStatusCode()).isEqualTo(HttpStatus.OK);
        JsonNode topHotelsBody = objectMapper.readTree(topHotels.getBody());
        assertThat(topHotelsBody.isArray()).isTrue();
        assertThat(topHotelsBody.size()).isGreaterThanOrEqualTo(2);
        assertThat(topHotelsBody.get(0).get("hotelId").asLong()).isEqualTo(hotelA.getId());
        assertThat(topHotelsBody.get(0).get("hotelName").asText()).isEqualTo("Hotel Magic Park");
        assertThat(topHotelsBody.get(0).get("revenue").asDouble()).isEqualTo(500.0);

        assertThat(topHotelsBody.get(1).get("hotelId").asLong()).isEqualTo(hotelB.getId());
        assertThat(topHotelsBody.get(1).get("hotelName").asText()).isEqualTo("Hotel Adventure");
        assertThat(topHotelsBody.get(1).get("revenue").asDouble()).isEqualTo(100.0);

        ResponseEntity<String> summary = restClient()
                .get()
                .uri("/api/dashboard/summary?year=2026")
                .retrieve()
                .toEntity(String.class);
        assertThat(summary.getStatusCode()).isEqualTo(HttpStatus.OK);
        JsonNode summaryBody = objectMapper.readTree(summary.getBody());
        assertThat(summaryBody.get("year").asInt()).isEqualTo(2026);
        assertThat(summaryBody.get("totalRevenue").asDouble()).isEqualTo(600.0);
        assertThat(summaryBody.get("ticketsByAgeRange").isArray()).isTrue();
        assertThat(summaryBody.get("topHotels").isArray()).isTrue();
    }

    private void createBooking(User user, Hotel hotel, LocalDateTime createdAt, BigDecimal totalPrice, List<TicketSeed> ticketSeeds) {
        Booking booking = Booking.builder()
                .user(user)
                .hotel(hotel)
                .boardType("FULL_BOARD")
                .visitDate(LocalDate.parse("2026-05-22"))
                .totalPrice(totalPrice)
                .emailSent(true)
                .build();

        List<Ticket> tickets = ticketSeeds.stream()
                .map(seed -> Ticket.builder()
                        .booking(booking)
                        .holderFullName(seed.holderFullName())
                        .ageRange(seed.ageRange())
                        .price(seed.price())
                        .build())
                .toList();

        booking.setTickets(tickets);
        Booking saved = bookingDashboardRepository.save(booking);
        jdbcTemplate.update(
                "update bookings set created_at = ? where id = ?",
                Timestamp.valueOf(createdAt),
                saved.getId()
        );
    }

    private record TicketSeed(String holderFullName, String ageRange, BigDecimal price) {
    }

    private RestClient restClient() {
        return RestClient.builder()
                .baseUrl("http://localhost:" + port)
                .defaultStatusHandler(HttpStatusCode::isError, (request, response) -> {
                })
                .build();
    }
}
