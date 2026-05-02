package com.parque.user.service;

import com.parque.user.dto.UserCreateRequest;
import com.parque.user.dto.UserResponse;
import com.parque.user.dto.UserUpdateRequest;

import java.util.List;

public interface UserService {
    List<UserResponse> getAll();

    UserResponse getById(Long id);

    UserResponse getByUsername(String username);

    UserResponse create(UserCreateRequest request);

    UserResponse update(Long id, UserUpdateRequest request);

    void delete(Long id);
}

