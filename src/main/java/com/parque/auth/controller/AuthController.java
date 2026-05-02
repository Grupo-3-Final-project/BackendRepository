package com.parque.auth.controller;

import com.parque.security.JwtProvider;
import com.parque.security.dto.LoginRequest;
import com.parque.security.dto.LoginResponse;
import com.parque.user.service.UserService;
import com.parque.user.dto.UserResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final JwtProvider jwtProvider;
    private final UserService userService;

    public AuthController(JwtProvider jwtProvider, UserService userService) {
        this.jwtProvider = jwtProvider;
        this.userService = userService;
    }

    /**
     * Login endpoint
     * POST /api/auth/login
     * @param loginRequest containing username and password
     * @return JWT token and user info
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest loginRequest) {
        try {
            // Find user by username (assuming UserService has this method)
            // For now, we'll try to find user by ID 1 as demo
            UserResponse user = userService.getByUsername(loginRequest.getUsername());

            if (user == null) {
                return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(new ErrorResponse("Invalid username or password", "INVALID_CREDENTIALS"));
            }

            // Generate JWT token
            String token = jwtProvider.generateToken(user.getId(), user.getUsername());

            LoginResponse response = LoginResponse.of(
                token,
                user.getId(),
                user.getUsername(),
                user.getEmail()
            );

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse("Login failed: " + e.getMessage(), "LOGIN_ERROR"));
        }
    }

    @lombok.Data
    @lombok.AllArgsConstructor
    static class ErrorResponse {
        private String message;
        private String code;
    }
}
