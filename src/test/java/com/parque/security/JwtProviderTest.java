package com.parque.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.assertThat;

class JwtProviderTest {

    private JwtProvider jwtProvider;

    @BeforeEach
    void setUp() {
        jwtProvider = new JwtProvider();
        ReflectionTestUtils.setField(jwtProvider, "jwtSecret", "my-secret-key-for-parque-atracciones-app-2024-make-it-long-and-secure");
        ReflectionTestUtils.setField(jwtProvider, "jwtExpirationMs", 3_600_000);
    }

    @Test
    void shouldGenerateAndReadJwtClaims() {
        String token = jwtProvider.generateToken(5L, "admin", "ADMIN");

        assertThat(jwtProvider.validateToken(token)).isTrue();
        assertThat(jwtProvider.getUsernameFromToken(token)).isEqualTo("admin");
        assertThat(jwtProvider.getCredentialIdFromToken(token)).isEqualTo(5L);
        assertThat(jwtProvider.getRoleFromToken(token)).isEqualTo("ADMIN");
        assertThat(jwtProvider.isTokenExpired(token)).isFalse();
        assertThat(jwtProvider.getExpirationDateFromToken(token)).isAfter(new java.util.Date());
    }

    @Test
    void shouldRejectMalformedTokens() {
        assertThat(jwtProvider.validateToken("invalid-token")).isFalse();
        assertThat(jwtProvider.isTokenExpired("invalid-token")).isTrue();
    }
}
