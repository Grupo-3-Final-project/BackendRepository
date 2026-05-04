package com.parque.attraction.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.parque.attraction.model.Attraction;

public interface AttractionRepository extends JpaRepository<Attraction, Long> {
}

