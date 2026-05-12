package com.parque.weather.controller;

import com.parque.weather.dto.GranadaWeatherResponse;
import com.parque.weather.service.GranadaWeatherService;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/weather")
@SecurityRequirements
public class WeatherController {

    private final GranadaWeatherService granadaWeatherService;

    public WeatherController(GranadaWeatherService granadaWeatherService) {
        this.granadaWeatherService = granadaWeatherService;
    }

    @GetMapping("/granada")
    public GranadaWeatherResponse getGranadaWeather() {
        return granadaWeatherService.getCurrentWeather();
    }
}
