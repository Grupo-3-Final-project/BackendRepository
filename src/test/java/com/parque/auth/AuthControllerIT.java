package com.parque.auth;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.parque.entity.User;
import com.parque.testconfig.JacksonTestConfig;
import com.parque.user.repository.UserRepository;
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

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(JacksonTestConfig.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
class AuthControllerIT {

    @LocalServerPort
    private int port;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
    }

    @Test
    void postLogin_shouldReturn200AndLoginResponse() throws Exception {
        userRepository.save(User.builder()
                .firstName("David")
                .lastName("Navarro")
                .dni("12345678A")
                .email("david@example.com")
                .phone("600123123")
                .birthDate(LocalDate.parse("1990-04-15"))
                .build());

        String body = """
                {
                  "username": "david@example.com",
                  "password": "secret"
                }
                """;

        ResponseEntity<String> response = postJson("/api/auth/login", body);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        JsonNode json = objectMapper.readTree(response.getBody());
        assertThat(json.get("token").asText()).isNotBlank();
        assertThat(json.get("type").asText()).isEqualTo("Bearer");
        assertThat(json.get("username").asText()).isEqualTo("david@example.com");
        assertThat(json.get("email").asText()).isEqualTo("david@example.com");
    }

    @Test
    void postLogin_shouldReturn400WithApiError_whenInvalidBody() throws Exception {
        String body = """
                {
                  "username": "",
                  "password": ""
                }
                """;

        ResponseEntity<String> response = postJson("/api/auth/login", body);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        JsonNode json = objectMapper.readTree(response.getBody());
        assertThat(json.get("status").asInt()).isEqualTo(400);
        assertThat(json.get("error").asText()).isEqualTo("Bad Request");
        assertThat(json.get("message").asText()).isEqualTo("Invalid login data");
        assertThat(json.get("path").asText()).isEqualTo("/api/auth/login");
        assertThat(json.get("timestamp").asText()).isNotBlank();
    }

    @Test
    void postLogin_shouldReturn404WithApiError_whenUserDoesNotExist() throws Exception {
        String body = """
                {
                  "username": "missing@example.com",
                  "password": "secret"
                }
                """;

        ResponseEntity<String> response = postJson("/api/auth/login", body);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        JsonNode json = objectMapper.readTree(response.getBody());
        assertThat(json.get("status").asInt()).isEqualTo(404);
        assertThat(json.get("error").asText()).isEqualTo("Not Found");
        assertThat(json.get("message").asText()).isEqualTo("User not found");
        assertThat(json.get("path").asText()).isEqualTo("/api/auth/login");
        assertThat(json.get("timestamp").asText()).isNotBlank();
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
