package com.parque.auth.controller;

import com.parque.security.JwtProvider;
import com.parque.security.dto.LoginRequest;
import com.parque.security.dto.LoginResponse;
import com.parque.user.service.UserService;
import com.parque.user.dto.UserResponse;
import jakarta.validation.Valid;
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

    @PostMapping("/login")
    public LoginResponse login(@Valid @RequestBody LoginRequest loginRequest) {
        UserResponse user = userService.getByUsername(loginRequest.getUsername());
        String token = jwtProvider.generateToken(user.id(), user.email());
        return LoginResponse.of(
                token,
                user.id(),
                user.email(),
                user.email()
        );
    }
}
