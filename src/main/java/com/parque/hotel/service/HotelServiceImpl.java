package com.parque.hotel.service;

import com.parque.entity.Hotel;
import com.parque.exception.ResourceNotFoundException;
import com.parque.hotel.dto.HotelCreateRequest;
import com.parque.hotel.dto.HotelResponse;
import com.parque.hotel.dto.HotelUpdateRequest;
import com.parque.hotel.repository.HotelRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class HotelServiceImpl implements HotelService {

    @Autowired
    public HotelServiceImpl(HotelRepository hotelRepository) {
        this.hotelRepository = hotelRepository;
    }

    private final HotelRepository hotelRepository;

@Override
@Transactional(readOnly = true)
public List<HotelResponse> getAll() {
    return hotelRepository.findAll()
            .stream()
            .map(this::toResponse)
            .toList();
}

    @Override
    @Transactional(readOnly = true)
    public HotelResponse getById(Long id) {
        Hotel hotel = hotelRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Hotel not found"));
        return toResponse(hotel);
    }

    @Override
    public HotelResponse create(HotelCreateRequest request) {
        Hotel hotel = Hotel.builder()
                .name(request.name())
                .description(request.description())
                .totalRooms(request.totalRooms())
                .availableRooms(request.availableRooms())
                .totalPlaces(request.totalPlaces())
                .availablePlaces(request.availablePlaces())
                .halfBoardPrice(request.halfBoardPrice())
                .fullBoardPrice(request.fullBoardPrice())
                .imageUrl(request.imageUrl())
                .build();

        return toResponse(hotelRepository.save(hotel));
    }

    @Override
    public HotelResponse update(Long id, HotelUpdateRequest request) {
        Hotel hotel = hotelRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Hotel not found"));

        hotel.setName(request.name());
        hotel.setDescription(request.description());
        hotel.setTotalRooms(request.totalRooms());
        hotel.setAvailableRooms(request.availableRooms());
        hotel.setTotalPlaces(request.totalPlaces());
        hotel.setAvailablePlaces(request.availablePlaces());
        hotel.setHalfBoardPrice(request.halfBoardPrice());
        hotel.setFullBoardPrice(request.fullBoardPrice());
        hotel.setImageUrl(request.imageUrl());

        return toResponse(hotelRepository.save(hotel));
    }

    @Override
    public void delete(Long id) {
        if (!hotelRepository.existsById(id)) {
            throw new ResourceNotFoundException("Hotel not found");
        }
        hotelRepository.deleteById(id);
    }

    private HotelResponse toResponse(Hotel hotel) {
        return new HotelResponse(
                hotel.getId(),
                hotel.getName(),
                hotel.getDescription(),
                hotel.getTotalRooms(),
                hotel.getAvailableRooms(),
                hotel.getTotalPlaces(),
                hotel.getAvailablePlaces(),
                hotel.getHalfBoardPrice(),
                hotel.getFullBoardPrice(),
                hotel.getImageUrl()
        );
    }
}

