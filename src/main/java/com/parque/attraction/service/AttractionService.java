package com.parque.attraction.service;

import com.parque.attraction.dto.AttractionCreateRequest;
import com.parque.attraction.dto.AttractionResponse;
import com.parque.attraction.dto.AttractionUpdateRequest;

import java.util.List;

public interface AttractionService {
    List<AttractionResponse> getAll();

    AttractionResponse getById(Long id);

    AttractionResponse create(AttractionCreateRequest request);

    AttractionResponse update(Long id, AttractionUpdateRequest request);

    void delete(Long id);
}

