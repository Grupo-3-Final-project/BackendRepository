package com.parque.offer.service;

import com.parque.offer.dto.OfferCreateRequest;
import com.parque.offer.dto.OfferResponse;

import java.util.List;

public interface OfferService {
    List<OfferResponse> getAll();

    OfferResponse getById(Long id);

    OfferResponse create(OfferCreateRequest request);
}

