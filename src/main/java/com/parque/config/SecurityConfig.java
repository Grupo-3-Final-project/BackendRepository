package com.parque.config;

import com.parque.auth.model.InternalRole;
import com.parque.security.ApiAuthenticationEntryPoint;
import com.parque.security.filter.JwtAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final ApiAuthenticationEntryPoint apiAuthenticationEntryPoint;

    public SecurityConfig(
            JwtAuthenticationFilter jwtAuthenticationFilter,
            ApiAuthenticationEntryPoint apiAuthenticationEntryPoint
    ) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
        this.apiAuthenticationEntryPoint = apiAuthenticationEntryPoint;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, CorsConfigurationSource corsConfigurationSource) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .exceptionHandling(exceptionHandling -> exceptionHandling.authenticationEntryPoint(apiAuthenticationEntryPoint))
            .cors(cors -> cors.configurationSource(corsConfigurationSource))
            .authorizeHttpRequests(authz -> authz
                // ========== PUBLIC ENDPOINTS (No authentication required) ==========
                // Authentication
                .requestMatchers(HttpMethod.POST, "/api/v1/auth/login").permitAll()
                .requestMatchers(HttpMethod.POST, "/api/v1/auth/refresh").permitAll()
                // User Registration
                .requestMatchers(HttpMethod.POST, "/api/v1/users").permitAll()
                // Public Resources (Read-only)
                .requestMatchers(HttpMethod.GET, "/api/v1/hotels", "/api/v1/hotels/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/v1/attractions", "/api/v1/attractions/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/v1/offers", "/api/v1/offers/**").permitAll()
                // API Documentation
                .requestMatchers("/swagger-ui/**", "/swagger-ui.html", "/v3/api-docs/**").permitAll()
                .requestMatchers("/actuator/**").permitAll()
                
                // ========== ADMIN ENDPOINTS ==========
                .requestMatchers("/api/v1/admin/**").hasRole(InternalRole.ADMIN.name())
                .requestMatchers("/api/v1/dashboard/**").hasRole(InternalRole.ADMIN.name())
                .requestMatchers("/api/v1/maintenance/**").hasRole(InternalRole.ADMIN.name())
                .requestMatchers("/api/v1/employees/**").hasRole(InternalRole.ADMIN.name())
                .requestMatchers("/api/v1/shifts/**").hasRole(InternalRole.ADMIN.name())
                .requestMatchers(HttpMethod.PUT, "/api/v1/attractions/**").hasRole(InternalRole.ADMIN.name())
                .requestMatchers(HttpMethod.DELETE, "/api/v1/attractions/**").hasRole(InternalRole.ADMIN.name())
                
                // ========== EMPLOYEE ENDPOINTS ==========
                .requestMatchers(HttpMethod.POST, "/api/v1/bookings/*/confirm", "/api/v1/bookings/*/cancel")
                    .hasAnyRole(InternalRole.EMPLOYEE.name(), InternalRole.MANAGER.name(), InternalRole.ADMIN.name())
                .requestMatchers(HttpMethod.PATCH, "/api/v1/attractions/*/status")
                    .hasAnyRole(InternalRole.EMPLOYEE.name(), InternalRole.MANAGER.name(), InternalRole.ADMIN.name())
                
                // ========== USER ENDPOINTS (Authenticated users) ==========
                .requestMatchers(HttpMethod.GET, "/api/v1/users/*/profile")
                    .hasAnyRole(InternalRole.USER.name(), InternalRole.MANAGER.name(), InternalRole.ADMIN.name())
                .requestMatchers(HttpMethod.POST, "/api/v1/bookings")
                    .hasAnyRole(InternalRole.USER.name(), InternalRole.MANAGER.name(), InternalRole.ADMIN.name())
                .requestMatchers(HttpMethod.GET, "/api/v1/bookings")
                    .hasAnyRole(InternalRole.USER.name(), InternalRole.MANAGER.name(), InternalRole.ADMIN.name())
                .requestMatchers(HttpMethod.GET, "/api/v1/bookings/**")
                    .hasAnyRole(InternalRole.USER.name(), InternalRole.MANAGER.name(), InternalRole.ADMIN.name())
                
                // ========== DENY ALL OTHER REQUESTS ==========
                .anyRequest().denyAll()
            )
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList(
            "http://localhost:3000",
            "http://localhost:3001",
            "http://localhost:4173",
            "http://localhost:5173",
            "http://127.0.0.1:3000",
            "http://127.0.0.1:3001",
            "http://127.0.0.1:4173",
            "http://127.0.0.1:5173",
            "https://parque-atracciones.com",
            "https://www.parque-atracciones.com"
        ));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/api/**", configuration);
        source.registerCorsConfiguration("/swagger-ui/**", new CorsConfiguration()
                .applyPermitDefaultValues()
        );
        source.registerCorsConfiguration("/v3/api-docs/**", new CorsConfiguration()
                .applyPermitDefaultValues()
        );

        return source;
    }
}
