package com.parque.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI parqueOpenApi() {
        return new OpenAPI()
                .info(new Info()
                        .title("API Parque de Atracciones")
                        .description("Backend Spring Boot del proyecto final de parque de atracciones.")
                        .version("1.0.0"));
    }
}
