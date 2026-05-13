package com.parque.attraction.repository;

import com.parque.attraction.model.Attraction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AttractionRepository extends JpaRepository<Attraction, Long> {

    Optional<Attraction> findByName(String name);
}

