package com.parque.weather.dto;

import java.time.LocalDateTime;

public record GranadaWeatherResponse(
        String city,
        Double temperatureCelsius,
        Double apparentTemperatureCelsius,
        String condition,
        Boolean day,
        LocalDateTime updatedAt
) {
}
