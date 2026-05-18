package com.parque.employee;

import com.parque.auth.repository.InternalCredentialRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.parque.testconfig.JacksonTestConfig;
import com.parque.employee.dto.EmployeeCreateRequest;
import com.parque.employee.dto.EmployeeUpdateRequest;
import com.parque.employee.repository.EmployeeRepository;
import com.parque.testsupport.InternalAuthSupport;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.web.client.RestClient;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(JacksonTestConfig.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
class EmployeeControllerIT {

    @LocalServerPort
    private int port;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private InternalCredentialRepository internalCredentialRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void setUp() {
        employeeRepository.deleteAll();
        InternalAuthSupport.ensureAdminCredential(internalCredentialRepository, passwordEncoder);
    }

    @Test
    void postEmployees_shouldReturn201AndEmployeeResponse() throws Exception {
        EmployeeCreateRequest request = new EmployeeCreateRequest(
                "Laura",
                "Gomez",
                "87654321B",
                "laura@example.com",
                "TECHNICIAN",
                "MORNING",
                true
        );

        ResponseEntity<String> response = postJson("/api/employees", objectMapper.writeValueAsString(request));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        JsonNode body = objectMapper.readTree(response.getBody());
        assertThat(body.get("id").isNumber()).isTrue();
        assertThat(body.get("dni").asText()).isEqualTo("87654321B");
        assertThat(body.get("employeeType").asText()).isEqualTo("TECHNICIAN");
        assertThat(body.get("shift").asText()).isEqualTo("MORNING");
        assertThat(body.get("active").asBoolean()).isTrue();
    }

    @Test
    void getEmployees_shouldReturn200AndList() throws Exception {
        EmployeeCreateRequest request = new EmployeeCreateRequest(
                "Laura",
                "Gomez",
                "87654321B",
                "laura@example.com",
                "TECHNICIAN",
                "MORNING",
                true
        );
        ResponseEntity<String> createdResponse = postJson("/api/employees", objectMapper.writeValueAsString(request));
        assertThat(createdResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);

        ResponseEntity<String> response = restClient()
                .get()
                .uri("/api/employees")
                .headers(httpHeaders -> httpHeaders.setBearerAuth(authToken()))
                .retrieve()
                .toEntity(String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        JsonNode body = objectMapper.readTree(response.getBody());
        assertThat(body.isArray()).isTrue();
        assertThat(body.size()).isGreaterThanOrEqualTo(1);
        List<String> fields = List.of(
                "id",
                "firstName",
                "lastName",
                "dni",
                "email",
                "employeeType",
                "shift",
                "active"
        );
        for (String field : fields) {
            assertThat(body.get(0).hasNonNull(field)).isTrue();
        }
    }

    @Test
    void putEmployees_shouldReturn200AndUpdatedEmployee() throws Exception {
        EmployeeCreateRequest createRequest = new EmployeeCreateRequest(
                "Laura",
                "Gomez",
                "87654321B",
                "laura@example.com",
                "TECHNICIAN",
                "MORNING",
                true
        );
        ResponseEntity<String> createdResponse = postJson("/api/employees", objectMapper.writeValueAsString(createRequest));
        assertThat(createdResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        JsonNode createdBody = objectMapper.readTree(createdResponse.getBody());
        long id = createdBody.get("id").asLong();

        EmployeeUpdateRequest updateRequest = new EmployeeUpdateRequest(
                "Laura",
                "Gomez Perez",
                "87654321B",
                "laura@example.com",
                "TECHNICIAN",
                "AFTERNOON",
                true
        );

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_TYPE, "application/json");
        headers.setBearerAuth(authToken());
        ResponseEntity<String> response = restClient()
                .put()
                .uri("/api/employees/" + id)
                .headers(httpHeaders -> httpHeaders.addAll(headers))
                .body(objectMapper.writeValueAsString(updateRequest))
                .retrieve()
                .toEntity(String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        JsonNode body = objectMapper.readTree(response.getBody());
        assertThat(body.get("id").asLong()).isEqualTo(id);
        assertThat(body.get("lastName").asText()).isEqualTo("Gomez Perez");
        assertThat(body.get("shift").asText()).isEqualTo("AFTERNOON");
    }

    @Test
    void postEmployees_shouldReturn400WithApiError_whenInvalidBody() throws Exception {
        String invalidBody = """
                {
                  "firstName": "",
                  "lastName": "",
                  "dni": "123",
                  "email": "bad-email",
                  "employeeType": "",
                  "shift": "",
                  "active": null
                }
                """;

        ResponseEntity<String> response = postJson("/api/employees", invalidBody);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        JsonNode body = objectMapper.readTree(response.getBody());
        assertThat(body.get("message").asText()).isEqualTo("Invalid employee data");
        assertThat(body.get("path").asText()).isEqualTo("/api/employees");
    }

    @Test
    void postEmployees_shouldReturn400WithApiError_whenEnumValueIsInvalid() throws Exception {
        String invalidBody = """
                {
                  "firstName": "Laura",
                  "lastName": "Gomez",
                  "dni": "87654321B",
                  "email": "laura@example.com",
                  "employeeType": "MANAGER",
                  "shift": "NIGHT",
                  "active": true
                }
                """;

        ResponseEntity<String> response = postJson("/api/employees", invalidBody);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        JsonNode body = objectMapper.readTree(response.getBody());
        assertThat(body.get("message").asText()).isEqualTo("Invalid employee data");
        assertThat(body.get("path").asText()).isEqualTo("/api/employees");
    }

    @Test
    void postEmployees_shouldReturn409WithApiError_whenDuplicateEmail() throws Exception {
        EmployeeCreateRequest first = new EmployeeCreateRequest(
                "Laura",
                "Gomez",
                "87654321B",
                "laura@example.com",
                "TECHNICIAN",
                "MORNING",
                true
        );
        EmployeeCreateRequest second = new EmployeeCreateRequest(
                "Ana",
                "Garcia",
                "12345678A",
                "laura@example.com",
                "CLEANER",
                "AFTERNOON",
                true
        );

        ResponseEntity<String> firstResponse = postJson("/api/employees", objectMapper.writeValueAsString(first));
        assertThat(firstResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);

        ResponseEntity<String> response = postJson("/api/employees", objectMapper.writeValueAsString(second));
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        JsonNode body = objectMapper.readTree(response.getBody());
        assertThat(body.get("message").asText()).isEqualTo("Email already exists");
    }

    @Test
    void getEmployee_shouldReturn404WithApiError_whenNotFound() throws Exception {
        ResponseEntity<String> response = restClient()
                .get()
                .uri("/api/employees/999")
                .headers(httpHeaders -> httpHeaders.setBearerAuth(authToken()))
                .retrieve()
                .toEntity(String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        JsonNode body = objectMapper.readTree(response.getBody());
        assertThat(body.get("message").asText()).isEqualTo("Employee not found");
    }

    @Test
    void getEmployee_shouldReturn200_whenEmployeeExists() throws Exception {
        EmployeeCreateRequest request = new EmployeeCreateRequest(
                "Laura",
                "Gomez",
                "87654321B",
                "laura@example.com",
                "TECHNICIAN",
                "MORNING",
                true
        );
        ResponseEntity<String> createdResponse = postJson("/api/employees", objectMapper.writeValueAsString(request));
        long id = objectMapper.readTree(createdResponse.getBody()).get("id").asLong();

        ResponseEntity<String> response = restClient()
                .get()
                .uri("/api/employees/" + id)
                .headers(httpHeaders -> httpHeaders.setBearerAuth(authToken()))
                .retrieve()
                .toEntity(String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        JsonNode body = objectMapper.readTree(response.getBody());
        assertThat(body.get("id").asLong()).isEqualTo(id);
        assertThat(body.get("email").asText()).isEqualTo("laura@example.com");
    }

    @Test
    void deleteEmployee_shouldReturn204_whenEmployeeExists() throws Exception {
        EmployeeCreateRequest request = new EmployeeCreateRequest(
                "Laura",
                "Gomez",
                "87654321B",
                "laura@example.com",
                "TECHNICIAN",
                "MORNING",
                true
        );
        ResponseEntity<String> createdResponse = postJson("/api/employees", objectMapper.writeValueAsString(request));
        long id = objectMapper.readTree(createdResponse.getBody()).get("id").asLong();

        ResponseEntity<String> response = restClient()
                .method(HttpMethod.DELETE)
                .uri("/api/employees/" + id)
                .headers(httpHeaders -> httpHeaders.setBearerAuth(authToken()))
                .retrieve()
                .toEntity(String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        assertThat(employeeRepository.findById(id)).isEmpty();
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
