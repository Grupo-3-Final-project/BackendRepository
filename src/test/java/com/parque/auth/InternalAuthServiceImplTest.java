package com.parque.auth;

import com.parque.auth.model.InternalRole;
import com.parque.auth.repository.InternalCredentialRepository;
import com.parque.auth.service.InternalAuthServiceImpl;
import com.parque.entity.InternalCredential;
import com.parque.exception.UnauthorizedException;
import com.parque.security.JwtProvider;
import com.parque.security.filter.dto.LoginRequest;
import com.parque.security.filter.dto.LoginResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.time.Month;
import java.time.ZoneId;
import java.util.Date;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class InternalAuthServiceImplTest {

    @Mock
    private InternalCredentialRepository internalCredentialRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtProvider jwtProvider;

    private InternalAuthServiceImpl service;

    @BeforeEach
    void setUp() {
        service = new InternalAuthServiceImpl(internalCredentialRepository, passwordEncoder, jwtProvider);
    }

    @Test
    void login_shouldReturnTokenPayloadForActiveCredentials() {
        InternalCredential credential = InternalCredential.builder()
                .id(7L)
                .username("manager")
                .email("manager@parque.local")
                .passwordHash("encoded-password")
                .role(InternalRole.MANAGER)
                .active(true)
                .build();
        Date expirationDate = Date.from(LocalDateTime.of(2026, Month.MAY, 30, 9, 45)
                .atZone(ZoneId.systemDefault())
                .toInstant());

        when(internalCredentialRepository.findByUsername("manager")).thenReturn(Optional.of(credential));
        when(passwordEncoder.matches("plain-password", "encoded-password")).thenReturn(true);
        when(jwtProvider.generateToken(7L, "manager", "MANAGER")).thenReturn("jwt-token");
        when(jwtProvider.getExpirationDateFromToken("jwt-token")).thenReturn(expirationDate);

        LoginResponse response = service.login(new LoginRequest("manager", "plain-password"));

        assertThat(response.getToken()).isEqualTo("jwt-token");
        assertThat(response.getType()).isEqualTo("Bearer");
        assertThat(response.getCredentialId()).isEqualTo(7L);
        assertThat(response.getUsername()).isEqualTo("manager");
        assertThat(response.getEmail()).isEqualTo("manager@parque.local");
        assertThat(response.getRole()).isEqualTo("MANAGER");
        assertThat(response.getExpiresAt()).isEqualTo(LocalDateTime.of(2026, Month.MAY, 30, 9, 45));
    }

    @Test
    void login_shouldRejectUnknownCredentials() {
        when(internalCredentialRepository.findByUsername("manager")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.login(new LoginRequest("manager", "plain-password")))
                .isInstanceOf(UnauthorizedException.class)
                .hasMessage("Invalid credentials");
    }

    @Test
    void login_shouldRejectInactiveCredentials() {
        InternalCredential credential = InternalCredential.builder()
                .id(7L)
                .username("manager")
                .email("manager@parque.local")
                .passwordHash("encoded-password")
                .role(InternalRole.MANAGER)
                .active(false)
                .build();

        when(internalCredentialRepository.findByUsername("manager")).thenReturn(Optional.of(credential));

        assertThatThrownBy(() -> service.login(new LoginRequest("manager", "plain-password")))
                .isInstanceOf(UnauthorizedException.class)
                .hasMessage("Invalid credentials");
    }

    @Test
    void login_shouldRejectWrongPassword() {
        InternalCredential credential = InternalCredential.builder()
                .id(7L)
                .username("manager")
                .email("manager@parque.local")
                .passwordHash("encoded-password")
                .role(InternalRole.MANAGER)
                .active(true)
                .build();

        when(internalCredentialRepository.findByUsername("manager")).thenReturn(Optional.of(credential));
        when(passwordEncoder.matches("plain-password", "encoded-password")).thenReturn(false);

        assertThatThrownBy(() -> service.login(new LoginRequest("manager", "plain-password")))
                .isInstanceOf(UnauthorizedException.class)
                .hasMessage("Invalid credentials");
    }
}
