package com.parque.auth.service;

import com.parque.security.filter.dto.LoginRequest;
import com.parque.security.filter.dto.LoginResponse;

public interface InternalAuthService {
    LoginResponse login(LoginRequest loginRequest);
}
