package com.parque.attraction;

import com.parque.auth.repository.InternalCredentialRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.parque.attraction.dto.AttractionCreateRequest;
import com.parque.attraction.dto.AttractionUpdateRequest;
import com.parque.attraction.repository.AttractionRepository;
import com.parque.testconfig.JacksonTestConfig;
import com.parque.testsupport.InternalAuthSupport;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.context.annotation.Import;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.client.RestClient;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(JacksonTestConfig.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
class AttractionControllerIT {

    @LocalServerPort
    private int port;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private AttractionRepository attractionRepository;

    @Autowired
    private InternalCredentialRepository internalCredentialRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void setUp() {
        attractionRepository.deleteAll();
        InternalAuthSupport.ensureAdminCredential(internalCredentialRepository, passwordEncoder);
    }

    @Test
    void postAttractions_shouldReturn201AndCalculatedFrequency() throws Exception {
        AttractionCreateRequest request = new AttractionCreateRequest(
                "Dragon Coaster",
                "Montana rusa principal del parque.",
                "LARGE",
                "OPEN",
                32,
                32,
                "https://example.com/attraction.jpg"
        );

        ResponseEntity<String> response = postJson("/api/attractions", objectMapper.writeValueAsString(request));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        JsonNode body = objectMapper.readTree(response.getBody());
        assertThat(body.get("id").isNumber()).isTrue();
        assertThat(body.get("maintenanceFrequencyDays").asInt()).isEqualTo(7);
    }

    @Test
    void getAttractions_shouldReturn200AndList() throws Exception {
        AttractionCreateRequest request = new AttractionCreateRequest(
                "Dragon Coaster",
                "Montana rusa principal del parque.",
                "LARGE",
                "OPEN",
                32,
                32,
                "https://example.com/attraction.jpg"
        );
        ResponseEntity<String> createdResponse = postJson("/api/attractions", objectMapper.writeValueAsString(request));
        assertThat(createdResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);

        ResponseEntity<String> response = restClient()
                .get()
                .uri("/api/attractions")
                .retrieve()
                .toEntity(String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        JsonNode body = objectMapper.readTree(response.getBody());
        assertThat(body.isArray()).isTrue();
        assertThat(body.size()).isGreaterThanOrEqualTo(1);
        List<String> fields = List.of(
                "id",
                "name",
                "description",
                "size",
                "status",
                "totalSeats",
                "availableSeats",
                "maintenanceFrequencyDays",
                "imageUrl"
        );
        for (String field : fields) {
            assertThat(body.get(0).hasNonNull(field)).isTrue();
        }
    }

    @Test
    void putAttractions_shouldReturn200AndUpdatedAttraction() throws Exception {
        AttractionCreateRequest createRequest = new AttractionCreateRequest(
                "Dragon Coaster",
                "Montana rusa principal del parque.",
                "LARGE",
                "OPEN",
                32,
                32,
                "https://example.com/attraction.jpg"
        );
        ResponseEntity<String> createdResponse = postJson("/api/attractions", objectMapper.writeValueAsString(createRequest));
        assertThat(createdResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        JsonNode createdBody = objectMapper.readTree(createdResponse.getBody());
        long id = createdBody.get("id").asLong();

        AttractionUpdateRequest updateRequest = new AttractionUpdateRequest(
                "Dragon Coaster",
                "Montana rusa principal del parque.",
                "LARGE",
                "MAINTENANCE",
                32,
                0,
                "https://example.com/attraction.jpg"
        );

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_TYPE, "application/json");
        headers.setBearerAuth(authToken());
        ResponseEntity<String> response = restClient()
                .put()
                .uri("/api/attractions/" + id)
                .headers(httpHeaders -> httpHeaders.addAll(headers))
                .body(objectMapper.writeValueAsString(updateRequest))
                .retrieve()
                .toEntity(String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        JsonNode body = objectMapper.readTree(response.getBody());
        assertThat(body.get("id").asLong()).isEqualTo(id);
        assertThat(body.get("status").asText()).isEqualTo("MAINTENANCE");
        assertThat(body.get("availableSeats").asInt()).isEqualTo(0);
        assertThat(body.get("maintenanceFrequencyDays").asInt()).isEqualTo(7);
    }

    @Test
    void postAttractions_shouldReturn400WithApiError_whenInvalidBody() throws Exception {
        String invalidBody = """
                {
                  "name": "",
                  "description": "",
                  "size": "",
                  "status": "",
                  "totalSeats": 0,
                  "availableSeats": -1,
                  "imageUrl": ""
                }
                """;

        ResponseEntity<String> response = postJson("/api/attractions", invalidBody);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        JsonNode body = objectMapper.readTree(response.getBody());
        assertThat(body.get("message").asText()).isEqualTo("Invalid attraction data");
        assertThat(body.get("path").asText()).isEqualTo("/api/attractions");
    }

    @Test
    void postAttractions_shouldReturn400WithApiError_whenEnumValueIsInvalid() throws Exception {
        String invalidBody = """
                {
                  "name": "Dragon Coaster",
                  "description": "Montana rusa principal del parque.",
                  "size": "HUGE",
                  "status": "BROKEN",
                  "totalSeats": 32,
                  "availableSeats": 20,
                  "imageUrl": "https://example.com/attraction.jpg"
                }
                """;

        ResponseEntity<String> response = postJson("/api/attractions", invalidBody);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        JsonNode body = objectMapper.readTree(response.getBody());
        assertThat(body.get("message").asText()).isEqualTo("Invalid attraction data");
        assertThat(body.get("path").asText()).isEqualTo("/api/attractions");
    }

    @Test
    void getAttraction_shouldReturn404WithApiError_whenNotFound() throws Exception {
        ResponseEntity<String> response = restClient()
                .get()
                .uri("/api/attractions/999")
                .retrieve()
                .toEntity(String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        JsonNode body = objectMapper.readTree(response.getBody());
        assertThat(body.get("message").asText()).isEqualTo("Attraction not found");
    }

    @Test
    void getLegacyAttractionsRoute_shouldReturn404WithApiError() throws Exception {
        ResponseEntity<String> response = restClient()
                .get()
                .uri("/api/v1/attractions")
                .retrieve()
                .toEntity(String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        JsonNode body = objectMapper.readTree(response.getBody());
        assertThat(body.get("status").asInt()).isEqualTo(404);
        assertThat(body.get("error").asText()).isEqualTo("Not Found");
        assertThat(body.get("message").asText()).isEqualTo("Resource not found");
        assertThat(body.get("path").asText()).isEqualTo("/api/v1/attractions");
    }

    private ResponseEntity<String> postJson(String path, String body) {
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_TYPE, "application/json");
        headers.setBearerAuth(authToken());
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

    private String authToken() {
        try {
            return InternalAuthSupport.login(restClient(), objectMapper);
        } catch (Exception exception) {
            throw new IllegalStateException(exception);
        }
    }
}
