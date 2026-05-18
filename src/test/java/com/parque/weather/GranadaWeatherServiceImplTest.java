package com.parque.weather;

import com.parque.exception.InternalServerErrorException;
import com.parque.weather.dto.GranadaWeatherResponse;
import com.parque.weather.service.GranadaWeatherServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
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

    @Test
    void shouldFallbackToNightConditionAndVariableWhenProviderReturnsUnknownCode() {
        RestClient.Builder builder = RestClient.builder();
        MockRestServiceServer server = MockRestServiceServer.bindTo(builder).build();
        server.expect(requestTo(org.hamcrest.Matchers.containsString("open-meteo.com")))
                .andRespond(withSuccess("""
                        {
                          "current": {
                            "time": "2026-05-18T23:00:00",
                            "temperature_2m": 19.0,
                            "apparent_temperature": 18.0,
                            "weather_code": 999,
                            "is_day": 0
                          }
                        }
                        """, MediaType.APPLICATION_JSON));

        GranadaWeatherServiceImpl service = new GranadaWeatherServiceImpl(builder.build());

        GranadaWeatherResponse response = service.getCurrentWeather();

        assertThat(response.day()).isFalse();
        assertThat(response.condition()).isEqualTo("Variable");
    }

    @Test
    void shouldRejectNullCurrentPayloadAsControlledInternalError() {
        RestClient.Builder builder = RestClient.builder();
        MockRestServiceServer server = MockRestServiceServer.bindTo(builder).build();
        server.expect(requestTo(org.hamcrest.Matchers.containsString("open-meteo.com")))
                .andRespond(withSuccess("""
                        {
                        }
                        """, MediaType.APPLICATION_JSON));

        GranadaWeatherServiceImpl service = new GranadaWeatherServiceImpl(builder.build());

        assertThatThrownBy(service::getCurrentWeather)
                .isInstanceOf(InternalServerErrorException.class)
                .hasMessage("Weather service unavailable");
    }

    @ParameterizedTest
    @CsvSource({
            "0,1,Despejado",
            "0,0,Noche despejada",
            "3,1,Nublado",
            "45,1,Niebla",
            "53,1,Llovizna",
            "61,1,Lluvia",
            "73,1,Nieve",
            "95,1,Tormenta"
    })
    void shouldResolveExpectedConditionsForKnownWeatherCodes(int weatherCode, int isDay, String expectedCondition) {
        RestClient.Builder builder = RestClient.builder();
        MockRestServiceServer server = MockRestServiceServer.bindTo(builder).build();
        server.expect(requestTo(org.hamcrest.Matchers.containsString("open-meteo.com")))
                .andRespond(withSuccess("""
                        {
                          "current": {
                            "time": "2026-05-18T12:00:00",
                            "temperature_2m": 20.0,
                            "apparent_temperature": 19.5,
                            "weather_code": %s,
                            "is_day": %s
                          }
                        }
                        """.formatted(weatherCode, isDay), MediaType.APPLICATION_JSON));

        GranadaWeatherServiceImpl service = new GranadaWeatherServiceImpl(builder.build());

        GranadaWeatherResponse response = service.getCurrentWeather();

        assertThat(response.condition()).isEqualTo(expectedCondition);
    }

    @Test
    void shouldReturnNoDataConditionWhenWeatherCodeIsNull() {
        RestClient.Builder builder = RestClient.builder();
        MockRestServiceServer server = MockRestServiceServer.bindTo(builder).build();
        server.expect(requestTo(org.hamcrest.Matchers.containsString("open-meteo.com")))
                .andRespond(withSuccess("""
                        {
                          "current": {
                            "time": "2026-05-18T12:00:00",
                            "temperature_2m": 21.0,
                            "apparent_temperature": 20.0,
                            "weather_code": null,
                            "is_day": null
                          }
                        }
                        """, MediaType.APPLICATION_JSON));

        GranadaWeatherServiceImpl service = new GranadaWeatherServiceImpl(builder.build());

        GranadaWeatherResponse response = service.getCurrentWeather();

        assertThat(response.condition()).isEqualTo("Sin datos");
        assertThat(response.day()).isFalse();
    }
}
