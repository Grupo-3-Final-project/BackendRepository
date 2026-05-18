package com.parque.weather;

import com.parque.exception.InternalServerErrorException;
import com.parque.weather.dto.GranadaWeatherResponse;
import com.parque.weather.service.GranadaWeatherServiceImpl;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.ExpectedCount;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

class GranadaWeatherServiceImplTest {

    @Test
    void shouldMapOpenMeteoPayloadAndReuseCache() {
        RestClient.Builder builder = RestClient.builder();
        MockRestServiceServer server = MockRestServiceServer.bindTo(builder).build();
        server.expect(ExpectedCount.once(), requestTo(org.hamcrest.Matchers.containsString("open-meteo.com")))
                .andRespond(withSuccess("""
                        {
                          "current": {
                            "time": "2026-05-18T12:00:00",
                            "temperature_2m": 26.4,
                            "apparent_temperature": 28.1,
                            "weather_code": 1,
                            "is_day": 1
                          }
                        }
                        """, MediaType.APPLICATION_JSON));

        GranadaWeatherServiceImpl service = new GranadaWeatherServiceImpl(builder.build());

        GranadaWeatherResponse first = service.getCurrentWeather();
        GranadaWeatherResponse second = service.getCurrentWeather();

        assertThat(first.city()).isEqualTo("Granada");
        assertThat(first.temperatureCelsius()).isEqualTo(26.4);
        assertThat(first.apparentTemperatureCelsius()).isEqualTo(28.1);
        assertThat(first.condition()).isEqualTo("Poco nuboso");
        assertThat(first.day()).isTrue();
        assertThat(second).isSameAs(first);

        server.verify();
    }

    @Test
    void shouldWrapProviderFailuresAsControlledInternalError() {
        RestClient.Builder builder = RestClient.builder();
        MockRestServiceServer server = MockRestServiceServer.bindTo(builder).build();
        server.expect(requestTo(org.hamcrest.Matchers.containsString("open-meteo.com")))
                .andRespond(withStatus(HttpStatus.INTERNAL_SERVER_ERROR));

        GranadaWeatherServiceImpl service = new GranadaWeatherServiceImpl(builder.build());

        assertThatThrownBy(service::getCurrentWeather)
                .isInstanceOf(InternalServerErrorException.class)
                .hasMessage("Weather service unavailable");
    }
}
