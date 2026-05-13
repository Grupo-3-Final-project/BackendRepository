package com.parque.offer.service;

import com.parque.offer.dto.OfferCreateRequest;
import com.parque.offer.dto.OfferResponse;
import com.parque.offer.dto.OfferUpdateRequest;

import java.util.List;

public interface OfferService {
    List<OfferResponse> getAll();

    OfferResponse getById(Long id);

    OfferResponse create(OfferCreateRequest request);

    OfferResponse update(Long id, OfferUpdateRequest request);

    void delete(Long id);
}

