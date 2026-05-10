package com.parque.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI parqueOpenApi() {
        return new OpenAPI()
                .info(new Info()
                        .title("Parque de Atracciones API")
                        .version("1.0.0")
                        .description("Backend API para el sistema de taquilla y administración del Parque de Atracciones. " +
                                "Proporciona endpoints para gestión de usuarios, hoteles, atracciones, reservas y dashboard administrativo.")
                        .contact(new Contact()
                                .name("Equipo de Desarrollo")
                                .url("https://github.com/Grupo-3-Final-project"))
                        .license(new License()
                                .name("MIT")
                                .url("https://opensource.org/licenses/MIT")))
                .servers(List.of(
                        new Server()
                                .url("http://localhost:8080")
                                .description("Servidor local de desarrollo"),
                        new Server()
                                .url("http://127.0.0.1:8080")
                                .description("Servidor local alternativo")))
                .components(new Components()
                        .addSecuritySchemes("bearerAuth",
                                new SecurityScheme()
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")
                                        .description("Token JWT obtenido en POST /api/auth/login")))
                .addSecurityItem(new SecurityRequirement().addList("bearerAuth"));
    }
}
