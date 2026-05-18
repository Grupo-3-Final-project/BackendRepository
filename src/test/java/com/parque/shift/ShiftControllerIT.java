package com.parque.shift;

import com.parque.auth.repository.InternalCredentialRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.parque.employee.dto.EmployeeCreateRequest;
import com.parque.employee.repository.EmployeeRepository;
import com.parque.employee.service.EmployeeService;
import com.parque.shift.dto.ShiftGenerateRequest;
import com.parque.shift.repository.ShiftRepository;
import com.parque.testconfig.JacksonTestConfig;
import com.parque.testsupport.InternalAuthSupport;
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
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.web.client.RestClient;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(JacksonTestConfig.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
class ShiftControllerIT {

    @LocalServerPort
    private int port;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ShiftRepository shiftRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private InternalCredentialRepository internalCredentialRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void setUp() {
        shiftRepository.deleteAll();
        employeeRepository.deleteAll();
        InternalAuthSupport.ensureAdminCredential(internalCredentialRepository, passwordEncoder);
    }

    @Test
    void postGenerate_shouldReturn201Or409() throws Exception {
        ShiftGenerateRequest request = new ShiftGenerateRequest(LocalDate.parse("2026-05-01"), LocalDate.parse("2026-05-31"));
        ResponseEntity<String> conflict = postJson("/api/shifts/generate", objectMapper.writeValueAsString(request));
        assertThat(conflict.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        JsonNode conflictBody = objectMapper.readTree(conflict.getBody());
        assertThat(conflictBody.get("message").asText()).isEqualTo("Not enough employees to cover required shifts");

        for (int i = 0; i < 3; i++) {
            employeeService.create(new EmployeeCreateRequest("Cleaner" + i, "X", "1000000" + i + "A", "c" + i + "@e.com", "CLEANER", "MORNING", true));
            employeeService.create(new EmployeeCreateRequest("Animator" + i, "X", "2000000" + i + "A", "a" + i + "@e.com", "ANIMATOR", "MORNING", true));
            employeeService.create(new EmployeeCreateRequest("Tech" + i, "X", "3000000" + i + "A", "t" + i + "@e.com", "TECHNICIAN", "MORNING", true));
        }

        ResponseEntity<String> created = postJson("/api/shifts/generate", objectMapper.writeValueAsString(request));
        assertThat(created.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        JsonNode body = objectMapper.readTree(created.getBody());
        assertThat(body.get("message").asText()).isEqualTo("Shifts generated successfully");
        assertThat(body.get("totalGeneratedShifts").asInt()).isGreaterThan(0);
    }

    @Test
    void getShifts_shouldReturn200AndGeneratedShifts() throws Exception {
        for (int i = 0; i < 3; i++) {
            employeeService.create(new EmployeeCreateRequest("Cleaner" + i, "X", "1000000" + i + "A", "c" + i + "@e.com", "CLEANER", "MORNING", true));
            employeeService.create(new EmployeeCreateRequest("Animator" + i, "X", "2000000" + i + "A", "a" + i + "@e.com", "ANIMATOR", "MORNING", true));
            employeeService.create(new EmployeeCreateRequest("Tech" + i, "X", "3000000" + i + "A", "t" + i + "@e.com", "TECHNICIAN", "MORNING", true));
        }

        postJson(
                "/api/shifts/generate",
                objectMapper.writeValueAsString(new ShiftGenerateRequest(LocalDate.parse("2026-05-01"), LocalDate.parse("2026-05-31")))
        );

        ResponseEntity<String> response = restClient()
                .get()
                .uri("/api/shifts")
                .headers(httpHeaders -> httpHeaders.setBearerAuth(authToken()))
                .retrieve()
                .toEntity(String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        JsonNode body = objectMapper.readTree(response.getBody());
        assertThat(body.isArray()).isTrue();
        assertThat(body).isNotEmpty();
        for (String field : List.of("id", "employeeFullName", "employeeType", "shift", "startDate", "endDate")) {
            assertThat(body.get(0).has(field)).isTrue();
        }
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
