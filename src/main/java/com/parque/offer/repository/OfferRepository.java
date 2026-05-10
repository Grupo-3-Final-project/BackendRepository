package com.parque.offer.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.parque.offer.model.Offer;

public interface OfferRepository extends JpaRepository<Offer, Long> {
}

