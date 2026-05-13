package com.parque.weather;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.parque.testconfig.JacksonTestConfig;
import com.parque.weather.dto.GranadaWeatherResponse;
import com.parque.weather.service.GranadaWeatherService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.web.client.RestClient;

import java.time.LocalDateTime;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@Import(JacksonTestConfig.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
class WeatherControllerIT {

    @LocalServerPort
    private int port;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private GranadaWeatherService granadaWeatherService;

    @Test
    void getGranadaWeather_shouldReturn200AndWeatherPayload() throws Exception {
        when(granadaWeatherService.getCurrentWeather()).thenReturn(
                new GranadaWeatherResponse(
                        "Granada",
                        24.5,
                        26.0,
                        "Poco nuboso",
                        true,
                        LocalDateTime.parse("2026-05-12T12:00:00")
                )
        );

        ResponseEntity<String> response = restClient()
                .get()
                .uri("/api/weather/granada")
                .retrieve()
                .toEntity(String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        JsonNode body = objectMapper.readTree(response.getBody());
        assertThat(fieldNames(body)).containsExactly(
                "city",
                "temperatureCelsius",
                "apparentTemperatureCelsius",
                "condition",
                "day",
                "updatedAt"
        );
        assertThat(body.get("city").asText()).isEqualTo("Granada");
        assertThat(body.get("temperatureCelsius").asDouble()).isEqualTo(24.5);
        assertThat(body.get("apparentTemperatureCelsius").asDouble()).isEqualTo(26.0);
        assertThat(body.get("condition").asText()).isEqualTo("Poco nuboso");
        assertThat(body.get("day").asBoolean()).isTrue();
        assertThat(body.get("updatedAt").asText()).isEqualTo("2026-05-12T12:00:00");
    }

    private RestClient restClient() {
        return RestClient.builder()
                .baseUrl("http://localhost:" + port)
                .defaultStatusHandler(HttpStatusCode::isError, (request, response) -> {
                })
                .build();
    }

    private List<String> fieldNames(JsonNode node) {
        Set<String> fieldNames = new LinkedHashSet<>();
        node.fieldNames().forEachRemaining(fieldNames::add);
        return fieldNames.stream().toList();
    }
}
