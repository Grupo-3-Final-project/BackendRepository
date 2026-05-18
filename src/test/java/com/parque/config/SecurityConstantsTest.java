package com.parque.config;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNoException;

class SecurityConstantsTest {

    @Test
    void class_shouldBeInstantiable() {
        assertThatNoException().isThrownBy(SecurityConstants::new);
    }

    @Test
    void endpointGroups_shouldExposeExpectedRoutes() {
        assertThat(SecurityConstants.PUBLIC_ENDPOINTS)
                .contains("/api/auth/login", "/swagger-ui/**", "/v3/api-docs/**", "/actuator/**", "/error");
        assertThat(SecurityConstants.PUBLIC_READ_ENDPOINTS)
                .contains("/api/hotels", "/api/hotels/*", "/api/attractions", "/api/attractions/*", "/api/offers", "/api/offers/*");
        assertThat(SecurityConstants.USER_REGISTRATION_ENDPOINTS)
                .containsExactly("/api/users");
        assertThat(SecurityConstants.ADMIN_ENDPOINTS)
                .contains("/api/admin/**", "/api/dashboard/**", "/api/maintenance/**", "/api/employees/**", "/api/shifts/**");
        assertThat(SecurityConstants.EMPLOYEE_ENDPOINTS)
                .contains("/api/bookings/*/confirm", "/api/bookings/*/cancel", "/api/attractions/*/status");
        assertThat(SecurityConstants.USER_ENDPOINTS)
                .contains("/api/users/*/profile", "/api/bookings");
    }
}
