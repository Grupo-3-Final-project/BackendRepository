package com.parque.offer;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.parque.entity.Hotel;
import com.parque.hotel.repository.HotelRepository;
import com.parque.offer.dto.OfferCreateRequest;
import com.parque.offer.repository.OfferRepository;
import com.parque.testconfig.JacksonTestConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.web.client.RestClient;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(JacksonTestConfig.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
class OfferControllerIT {

    @LocalServerPort
    private int port;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private OfferRepository offerRepository;

    @Autowired
    private HotelRepository hotelRepository;

    private Long hotelId;

    @BeforeEach
    void setUp() {
        offerRepository.deleteAll();
        hotelRepository.deleteAll();

        Hotel hotel = Hotel.builder()
                .name("Hotel Magic Park")
                .description("Hotel familiar situado junto al parque.")
                .totalRooms(120)
                .availableRooms(120)
                .totalPlaces(240)
                .availablePlaces(240)
                .halfBoardPrice(new BigDecimal("80.0"))
                .fullBoardPrice(new BigDecimal("120.0"))
                .imageUrl("https://example.com/hotel.jpg")
                .build();

        hotelId = hotelRepository.save(hotel).getId();
    }

    @Test
    void postOffers_shouldReturn201AndOfferResponse() throws Exception {
        OfferCreateRequest request = new OfferCreateRequest(
                "Oferta Familiar Magic Park",
                "Hotel + entradas para 2 adultos y 2 ninos.",
                hotelId,
                "FULL_BOARD",
                4,
                new BigDecimal("399.99"),
                "https://example.com/offer.jpg"
        );

        ResponseEntity<String> response = postJson("/api/offers", objectMapper.writeValueAsString(request));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        JsonNode body = objectMapper.readTree(response.getBody());
        assertThat(body.get("id").isNumber()).isTrue();
        assertThat(body.get("title").asText()).isEqualTo("Oferta Familiar Magic Park");
        assertThat(body.get("hotelId").asLong()).isEqualTo(hotelId);
        assertThat(body.get("hotelName").asText()).isEqualTo("Hotel Magic Park");
        assertThat(body.get("boardType").asText()).isEqualTo("FULL_BOARD");
        assertThat(body.get("includedTickets").asInt()).isEqualTo(4);
        assertThat(body.get("totalPrice").asDouble()).isEqualTo(399.99);
        assertThat(body.get("imageUrl").asText()).isEqualTo("https://example.com/offer.jpg");
    }

    @Test
    void getOffers_shouldReturn200AndList() throws Exception {
        OfferCreateRequest request = new OfferCreateRequest(
                "Oferta Familiar Magic Park",
                "Hotel + entradas para 2 adultos y 2 ninos.",
                hotelId,
                "FULL_BOARD",
                4,
                new BigDecimal("399.99"),
                "https://example.com/offer.jpg"
        );
        ResponseEntity<String> createdResponse = postJson("/api/offers", objectMapper.writeValueAsString(request));
        assertThat(createdResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);

        ResponseEntity<String> response = restClient()
                .get()
                .uri("/api/offers")
                .retrieve()
                .toEntity(String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        JsonNode body = objectMapper.readTree(response.getBody());
        assertThat(body.isArray()).isTrue();
        assertThat(body.size()).isGreaterThanOrEqualTo(1);
        List<String> fields = List.of(
                "id",
                "title",
                "description",
                "hotelId",
                "hotelName",
                "boardType",
                "includedTickets",
                "totalPrice",
                "imageUrl"
        );
        for (String field : fields) {
            assertThat(body.get(0).hasNonNull(field)).isTrue();
        }
    }

    @Test
    void getOffer_shouldReturn404WithApiError_whenNotFound() throws Exception {
        ResponseEntity<String> response = restClient()
                .get()
                .uri("/api/offers/999")
                .retrieve()
                .toEntity(String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        JsonNode body = objectMapper.readTree(response.getBody());
        assertThat(body.get("message").asText()).isEqualTo("Offer not found");
        assertThat(body.get("path").asText()).isEqualTo("/api/offers/999");
    }

    @Test
    void postOffers_shouldReturn400WithApiError_whenInvalidBody() throws Exception {
        String invalidBody = """
                {
                  "title": "",
                  "description": "",
                  "hotelId": null,
                  "boardType": "",
                  "includedTickets": 0,
                  "totalPrice": 0,
                  "imageUrl": ""
                }
                """;

        ResponseEntity<String> response = postJson("/api/offers", invalidBody);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        JsonNode body = objectMapper.readTree(response.getBody());
        assertThat(body.get("message").asText()).isEqualTo("Invalid offer data");
        assertThat(body.get("path").asText()).isEqualTo("/api/offers");
    }

    @Test
    void postOffers_shouldReturn400WithApiError_whenBoardTypeIsInvalid() throws Exception {
        String invalidBody = """
                {
                  "title": "Oferta Familiar Magic Park",
                  "description": "Hotel + entradas para 2 adultos y 2 ninos.",
                  "hotelId": %d,
                  "boardType": "ROOM_ONLY",
                  "includedTickets": 4,
                  "totalPrice": 399.99,
                  "imageUrl": "https://example.com/offer.jpg"
                }
                """.formatted(hotelId);

        ResponseEntity<String> response = postJson("/api/offers", invalidBody);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        JsonNode body = objectMapper.readTree(response.getBody());
        assertThat(body.get("message").asText()).isEqualTo("Invalid offer data");
        assertThat(body.get("path").asText()).isEqualTo("/api/offers");
    }

    @Test
    void postOffers_shouldReturn404WithApiError_whenHotelNotFound() throws Exception {
        OfferCreateRequest request = new OfferCreateRequest(
                "Oferta Familiar Magic Park",
                "Hotel + entradas para 2 adultos y 2 ninos.",
                999L,
                "FULL_BOARD",
                4,
                new BigDecimal("399.99"),
                "https://example.com/offer.jpg"
        );

        ResponseEntity<String> response = postJson("/api/offers", objectMapper.writeValueAsString(request));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        JsonNode body = objectMapper.readTree(response.getBody());
        assertThat(body.get("message").asText()).isEqualTo("Hotel not found");
        assertThat(body.get("path").asText()).isEqualTo("/api/offers");
    }

    private ResponseEntity<String> postJson(String path, String body) {
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_TYPE, "application/json");
        return restClient()
                .post()
                .uri(path)
                .headers(httpHeaders -> httpHeaders.addAll(headers))
                .body(body)
                .retrieve()
                .toEntity(String.class);
    }

    private RestClient restClient() {
        return RestClient.builder()
                .baseUrl("http://localhost:" + port)
                .defaultStatusHandler(HttpStatusCode::isError, (request, response) -> {
                })
                .build();
    }
}

