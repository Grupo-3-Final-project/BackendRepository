package com.parque.auth.repository;

import com.parque.entity.InternalCredential;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface InternalCredentialRepository extends JpaRepository<InternalCredential, Long> {
    Optional<InternalCredential> findByUsername(String username);
}
