package com.parque.auth.service;

import com.parque.security.dto.LoginRequest;
import com.parque.security.dto.LoginResponse;

public interface InternalAuthService {
    LoginResponse login(LoginRequest loginRequest);
}
