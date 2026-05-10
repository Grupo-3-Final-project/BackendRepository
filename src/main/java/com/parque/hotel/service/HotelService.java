package com.parque.hotel.service;

import com.parque.hotel.dto.HotelCreateRequest;
import com.parque.hotel.dto.HotelResponse;
import com.parque.hotel.dto.HotelUpdateRequest;

import java.util.List;

public interface HotelService {
    List<HotelResponse> getAll();

    HotelResponse getById(Long id);

    HotelResponse create(HotelCreateRequest request);

    HotelResponse update(Long id, HotelUpdateRequest request);

    void delete(Long id);
}

