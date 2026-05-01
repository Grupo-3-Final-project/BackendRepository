package com.parque.dashboard;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClient;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Transactional
class DashboardControllerIT {

    @LocalServerPort
    private int port;

    @Autowired
    private ObjectMapper objectMapper;

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

    private RestClient restClient() {
        return RestClient.builder().baseUrl("http://localhost:" + port).build();
    }
}

