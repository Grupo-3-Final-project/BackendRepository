package com.parque.weather.service;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.parque.exception.InternalServerErrorException;
import com.parque.weather.dto.GranadaWeatherResponse;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;

@Service
public class GranadaWeatherServiceImpl implements GranadaWeatherService {

    private static final String GRANADA_WEATHER_URL =
            "https://api.open-meteo.com/v1/forecast?latitude=37.1773&longitude=-3.5986&current=temperature_2m,apparent_temperature,weather_code,is_day&timezone=Europe/Madrid";

    private static final Duration CACHE_TTL = Duration.ofMinutes(10);

    private final RestClient restClient;

    private GranadaWeatherResponse cachedResponse;
    private Instant cachedAt;

    public GranadaWeatherServiceImpl() {
        this(RestClient.create());
    }

    public GranadaWeatherServiceImpl(RestClient restClient) {
        this.restClient = restClient;
    }

    @Override
    public synchronized GranadaWeatherResponse getCurrentWeather() {
        if (cachedResponse != null && cachedAt != null && Instant.now().isBefore(cachedAt.plus(CACHE_TTL))) {
            return cachedResponse;
        }

        OpenMeteoResponse response;
        try {
            response = restClient.get()
                    .uri(GRANADA_WEATHER_URL)
                    .retrieve()
                    .body(OpenMeteoResponse.class);
        } catch (RestClientException exception) {
            throw new InternalServerErrorException("Weather service unavailable");
        }

        if (response == null || response.current() == null) {
            throw new InternalServerErrorException("Weather service unavailable");
        }

        LocalDateTime updatedAt = LocalDateTime.parse(response.current().time());
        boolean day = response.current().isDay() != null && response.current().isDay() == 1;

        cachedResponse = new GranadaWeatherResponse(
                "Granada",
                response.current().temperatureCelsius(),
                response.current().apparentTemperatureCelsius(),
                resolveCondition(response.current().weatherCode(), day),
                day,
                updatedAt
        );
        cachedAt = Instant.now();

        return cachedResponse;
    }

    private String resolveCondition(Integer weatherCode, boolean day) {
        if (weatherCode == null) {
            return "Sin datos";
        }

        return switch (weatherCode) {
            case 0 -> day ? "Despejado" : "Noche despejada";
            case 1, 2 -> "Poco nuboso";
            case 3 -> "Nublado";
            case 45, 48 -> "Niebla";
            case 51, 53, 55, 56, 57 -> "Llovizna";
            case 61, 63, 65, 66, 67, 80, 81, 82 -> "Lluvia";
            case 71, 73, 75, 77, 85, 86 -> "Nieve";
            case 95, 96, 99 -> "Tormenta";
            default -> "Variable";
        };
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    private record OpenMeteoResponse(CurrentWeather current) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    private record CurrentWeather(
            String time,
            @JsonProperty("temperature_2m")
            Double temperatureCelsius,
            @JsonProperty("apparent_temperature")
            Double apparentTemperatureCelsius,
            @JsonProperty("weather_code")
            Integer weatherCode,
            @JsonProperty("is_day")
            Integer isDay
    ) {
    }
}
