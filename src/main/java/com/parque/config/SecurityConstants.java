package com.parque.config;

/**
 * Security constants for API endpoints and role-based access control
 */
public class SecurityConstants {

    // Public endpoints (no authentication required)
    public static final String[] PUBLIC_ENDPOINTS = {
        "/api/auth/login",
        "/swagger-ui/**",
        "/swagger-ui.html",
        "/v3/api-docs/**",
        "/actuator/**",
        "/error"
    };

    // Read-only public endpoints (GET only, no authentication required)
    public static final String[] PUBLIC_READ_ENDPOINTS = {
        "/api/hotels",
        "/api/hotels/*",
        "/api/attractions",
        "/api/attractions/*",
        "/api/offers",
        "/api/offers/*"
    };

    // User registration endpoint (POST only, no authentication required)
    public static final String[] USER_REGISTRATION_ENDPOINTS = {
        "/api/users"
    };

    // Admin endpoints (ROLE_ADMIN required)
    public static final String[] ADMIN_ENDPOINTS = {
        "/api/admin/**",
        "/api/dashboard/**",
        "/api/maintenance/**",
        "/api/employees/**",
        "/api/shifts/**"
    };

    // Employee endpoints (ROLE_EMPLOYEE, ROLE_MANAGER, ROLE_ADMIN)
    public static final String[] EMPLOYEE_ENDPOINTS = {
        "/api/bookings/*/confirm",
        "/api/bookings/*/cancel",
        "/api/attractions/*/status"
    };

    // User endpoints (ROLE_USER, ROLE_MANAGER, ROLE_ADMIN)
    public static final String[] USER_ENDPOINTS = {
        "/api/users/*/profile",
        "/api/bookings"
    };
}
