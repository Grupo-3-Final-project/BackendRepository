package com.parque.maintenance;

import com.parque.auth.repository.InternalCredentialRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.parque.attraction.dto.AttractionCreateRequest;
import com.parque.attraction.repository.AttractionRepository;
import com.parque.attraction.service.AttractionService;
import com.parque.employee.dto.EmployeeCreateRequest;
import com.parque.employee.repository.EmployeeRepository;
import com.parque.employee.service.EmployeeService;
import com.parque.maintenance.dto.MaintenanceGenerateRequest;
import com.parque.maintenance.repository.MaintenanceRepository;
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

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(JacksonTestConfig.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
class MaintenanceControllerIT {

    @LocalServerPort
    private int port;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MaintenanceRepository maintenanceRepository;

    @Autowired
    private AttractionService attractionService;

    @Autowired
    private AttractionRepository attractionRepository;

    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private InternalCredentialRepository internalCredentialRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void setUp() {
        maintenanceRepository.deleteAll();
        attractionRepository.deleteAll();
        employeeRepository.deleteAll();
        InternalAuthSupport.ensureAdminCredential(internalCredentialRepository, passwordEncoder);
    }

    @Test
    void postGenerate_shouldReturn201Or409() throws Exception {
        attractionService.create(new AttractionCreateRequest("A", "D", "LARGE", "OPEN", 10, 10, "https://example.com/a.jpg"));

        MaintenanceGenerateRequest request = new MaintenanceGenerateRequest(LocalDate.parse("2026-05-01"), LocalDate.parse("2026-05-31"));
        ResponseEntity<String> conflict = postJson("/api/maintenance/generate", objectMapper.writeValueAsString(request));
        assertThat(conflict.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        JsonNode conflictBody = objectMapper.readTree(conflict.getBody());
        assertThat(conflictBody.get("message").asText()).isEqualTo("Not enough technicians available");

        employeeService.create(new EmployeeCreateRequest("T", "T", "87654321B", "t@example.com", "TECHNICIAN", "MORNING", true));
        ResponseEntity<String> created = postJson("/api/maintenance/generate", objectMapper.writeValueAsString(request));
        assertThat(created.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        JsonNode body = objectMapper.readTree(created.getBody());
        assertThat(body.get("message").asText()).isEqualTo("Maintenance schedule generated successfully");
        assertThat(body.get("totalMaintenanceTasks").asInt()).isGreaterThan(0);
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
