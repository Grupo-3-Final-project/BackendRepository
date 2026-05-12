package com.parque.booking;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.parque.attraction.model.Attraction;
import com.parque.attraction.repository.AttractionRepository;
import com.parque.booking.model.Booking;
import com.parque.booking.model.TicketStatus;
import com.parque.booking.repository.TicketAccessRepository;
import com.parque.booking.repository.BookingRepository;
import com.parque.entity.Ticket;
import com.parque.testconfig.JacksonTestConfig;
import com.parque.user.model.User;
import com.parque.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.web.client.RestClient;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(JacksonTestConfig.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
class TicketControllerIT {

    @LocalServerPort
    private int port;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AttractionRepository attractionRepository;

    @Autowired
    private TicketAccessRepository ticketAccessRepository;

    @BeforeEach
    void setUp() {
        bookingRepository.deleteAll();
        attractionRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void getMobileAccess_shouldReturn200AndSortedAttractions() throws Exception {
        attractionRepository.saveAll(List.of(
                saveAttraction("Splash River", "OPEN"),
                saveAttraction("Abyss Wheel", "MAINTENANCE")
        ));

        Ticket ticket = saveBookingWithTicket(
                "mobile-token-1",
                "entry-token-1",
                TicketStatus.VALID,
                LocalDate.now().plusDays(2),
                null
        );

        ResponseEntity<String> response = restClient()
                .get()
                .uri("/api/tickets/mobile/" + ticket.getMobileAccessToken())
                .retrieve()
                .toEntity(String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        JsonNode body = objectMapper.readTree(response.getBody());
        assertThat(fieldNames(body)).containsExactly(
                "ticketId",
                "bookingId",
                "holderFullName",
                "ticketStatus",
                "visitDate",
                "attractions"
        );
        assertThat(body.get("ticketId").asLong()).isEqualTo(ticket.getId());
        assertThat(body.get("bookingId").asLong()).isEqualTo(ticket.getBooking().getId());
        assertThat(body.get("holderFullName").asText()).isEqualTo("Ana Garcia");
        assertThat(body.get("ticketStatus").asText()).isEqualTo("VALID");
        assertThat(body.get("visitDate").asText()).isEqualTo(ticket.getBooking().getVisitDate().toString());
        assertThat(body.get("attractions").isArray()).isTrue();
        assertThat(body.get("attractions")).hasSize(2);
        assertThat(body.get("attractions").get(0).get("name").asText()).isEqualTo("Abyss Wheel");
        assertThat(body.get("attractions").get(1).get("name").asText()).isEqualTo("Splash River");
    }

    @Test
    void getMobileAccess_shouldReturn404WithApiError_whenTicketDoesNotExist() throws Exception {
        ResponseEntity<String> response = restClient()
                .get()
                .uri("/api/tickets/mobile/missing-token")
                .retrieve()
                .toEntity(String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        JsonNode body = objectMapper.readTree(response.getBody());
        assertErrorContract(body, 404, "Not Found", "Ticket not found", "/api/tickets/mobile/missing-token");
    }

    @Test
    void validateEntry_shouldReturn200AndMarkTicketAsUsed() throws Exception {
        Ticket ticket = saveBookingWithTicket(
                "mobile-token-2",
                "entry-token-2",
                TicketStatus.VALID,
                LocalDate.now(),
                null
        );

        ResponseEntity<String> response = restClient()
                .post()
                .uri("/api/tickets/entry/" + ticket.getEntryToken() + "/validate")
                .retrieve()
                .toEntity(String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        JsonNode body = objectMapper.readTree(response.getBody());
        assertThat(fieldNames(body)).containsExactly(
                "ticketId",
                "bookingId",
                "holderFullName",
                "ticketStatus",
                "visitDate",
                "usedAt"
        );
        assertThat(body.get("ticketId").asLong()).isEqualTo(ticket.getId());
        assertThat(body.get("ticketStatus").asText()).isEqualTo("USED");
        assertThat(body.get("usedAt").asText()).isNotBlank();

        Ticket savedTicket = ticketAccessRepository.findByEntryToken(ticket.getEntryToken())
                .orElseThrow();
        assertThat(savedTicket.getStatus()).isEqualTo(TicketStatus.USED);
        assertThat(savedTicket.getUsedAt()).isNotNull();
    }

    @Test
    void validateEntry_shouldReturn409WithApiError_whenTicketWasAlreadyUsed() throws Exception {
        Ticket ticket = saveBookingWithTicket(
                "mobile-token-3",
                "entry-token-3",
                TicketStatus.USED,
                LocalDate.now(),
                LocalDateTime.now().withNano(0)
        );

        ResponseEntity<String> response = restClient()
                .post()
                .uri("/api/tickets/entry/" + ticket.getEntryToken() + "/validate")
                .retrieve()
                .toEntity(String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        JsonNode body = objectMapper.readTree(response.getBody());
        assertErrorContract(body, 409, "Conflict", "Ticket already used", "/api/tickets/entry/entry-token-3/validate");
    }

    private Ticket saveBookingWithTicket(
            String mobileAccessToken,
            String entryToken,
            TicketStatus status,
            LocalDate visitDate,
            LocalDateTime usedAt
    ) {
        User user = userRepository.save(User.builder()
                .firstName("David")
                .lastName("Navarro")
                .dni(buildDni(mobileAccessToken))
                .email(mobileAccessToken + "@example.com")
                .phone("600123123")
                .birthDate(LocalDate.parse("1990-04-15"))
                .build());

        Booking booking = Booking.builder()
                .user(user)
                .boardType("FULL_BOARD")
                .visitDate(visitDate)
                .totalPrice(new BigDecimal("65.00"))
                .emailSent(true)
                .build();

        Ticket ticket = Ticket.builder()
                .booking(booking)
                .holderFullName("Ana Garcia")
                .ageRange("ADULT")
                .price(new BigDecimal("65.00"))
                .entryToken(entryToken)
                .mobileAccessToken(mobileAccessToken)
                .status(status)
                .usedAt(usedAt)
                .build();

        booking.setTickets(List.of(ticket));

        Booking savedBooking = bookingRepository.save(booking);
        return savedBooking.getTickets().getFirst();
    }

    private Attraction saveAttraction(String name, String status) {
        return Attraction.builder()
                .name(name)
                .description("Descripcion")
                .size("MEDIUM")
                .status(status)
                .totalSeats(20)
                .availableSeats(20)
                .maintenanceFrequencyDays(14)
                .imageUrl("https://example.com/" + name.toLowerCase().replace(" ", "-") + ".jpg")
                .build();
    }

    private String buildDni(String source) {
        int numericValue = Math.abs(source.hashCode()) % 100000000;
        return String.format("%08dA", numericValue);
    }

    private RestClient restClient() {
        return RestClient.builder()
                .baseUrl("http://localhost:" + port)
                .defaultStatusHandler(HttpStatusCode::isError, (request, response) -> {
                })
                .build();
    }

    private void assertErrorContract(JsonNode body, int status, String error, String message, String path) {
        assertThat(fieldNames(body)).containsExactly("status", "error", "message", "path", "timestamp");
        assertThat(body.get("status").asInt()).isEqualTo(status);
        assertThat(body.get("error").asText()).isEqualTo(error);
        assertThat(body.get("message").asText()).isEqualTo(message);
        assertThat(body.get("path").asText()).isEqualTo(path);
        assertThat(body.get("timestamp").asText()).isNotBlank();
    }

    private List<String> fieldNames(JsonNode node) {
        Set<String> fieldNames = new LinkedHashSet<>();
        node.fieldNames().forEachRemaining(fieldNames::add);
        return fieldNames.stream().toList();
    }
}
