package com.parque.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@MockitoSettings(strictness = Strictness.LENIENT)
class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler handler;

    @Mock
    private HttpServletRequest request;

    @BeforeEach
    void setUp() {
        handler = new GlobalExceptionHandler();
        request = org.mockito.Mockito.mock(HttpServletRequest.class);
    }

    @Test
    void shouldMapKnownExceptionsToExpectedStatuses() {
        when(request.getRequestURI()).thenReturn("/api/users");
        assertThat(handler.handleResourceNotFound(new ResourceNotFoundException("User not found"), request).getStatusCode())
                .isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(handler.handleConflict(new ConflictException("Hotel is full"), request).getStatusCode())
                .isEqualTo(HttpStatus.CONFLICT);
        assertThat(handler.handleUnauthorized(new UnauthorizedException("Authentication is required"), request).getStatusCode())
                .isEqualTo(HttpStatus.UNAUTHORIZED);
        assertThat(handler.handleIllegalArgument(new IllegalArgumentException("Invalid request data"), request).getStatusCode())
                .isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(handler.handleDataIntegrityViolation(new DataIntegrityViolationException("conflict"), request).getStatusCode())
                .isEqualTo(HttpStatus.CONFLICT);
        assertThat(handler.handleInternalServer(new InternalServerErrorException("Weather service unavailable"), request).getStatusCode())
                .isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Test
    void shouldResolveBadRequestMessagesByPath() {
        assertThat(messageFor("/api/users")).isEqualTo("Invalid user data");
        assertThat(messageFor("/api/hotels")).isEqualTo("Invalid hotel data");
        assertThat(messageFor("/api/attractions")).isEqualTo("Invalid attraction data");
        assertThat(messageFor("/api/employees")).isEqualTo("Invalid employee data");
        assertThat(messageFor("/api/bookings")).isEqualTo("Invalid booking data");
        assertThat(messageFor("/api/auth")).isEqualTo("Invalid login data");
        assertThat(messageFor("/api/shifts")).isEqualTo("Invalid request data");
        assertThat(messageFor("/api/maintenance")).isEqualTo("Invalid request data");
        assertThat(messageFor("/api/offers")).isEqualTo("Invalid offer data");
        assertThat(messageFor("/api/other")).isEqualTo("Invalid request data");
    }

    @Test
    void shouldHandleMissingResourceAndUnexpectedErrors() {
        when(request.getRequestURI()).thenReturn("/api/missing");

        ResponseEntity<ErrorResponseDTO> noResourceResponse = handler.handleNoResourceFound(
                new NoResourceFoundException(HttpMethod.GET, "/api/missing", null),
                request
        );
        ResponseEntity<ErrorResponseDTO> unexpectedResponse = handler.handleUnexpected(
                new RuntimeException("boom"),
                request
        );

        assertThat(noResourceResponse.getBody().message()).isEqualTo("Resource not found");
        assertThat(unexpectedResponse.getBody().message()).isEqualTo("Unexpected error");
    }

    private String messageFor(String path) {
        when(request.getRequestURI()).thenReturn(path);
        return handler.handleNotReadable(
                new org.springframework.http.converter.HttpMessageNotReadableException("bad", org.mockito.Mockito.mock(HttpInputMessage.class)),
                request
        )
                .getBody()
                .message();
    }
}
