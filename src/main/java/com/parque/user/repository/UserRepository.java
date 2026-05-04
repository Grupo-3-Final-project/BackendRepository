package com.parque.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.parque.user.model.User;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    boolean existsByEmail(String email);

    boolean existsByDni(String dni);

    Optional<User> findByEmail(String email);

    Optional<User> findByDni(String dni);
}

