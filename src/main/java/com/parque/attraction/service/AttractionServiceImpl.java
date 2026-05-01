package com.parque.attraction.service;

import com.parque.attraction.dto.AttractionCreateRequest;
import com.parque.attraction.dto.AttractionResponse;
import com.parque.attraction.dto.AttractionUpdateRequest;
import com.parque.attraction.repository.AttractionRepository;
import com.parque.entity.Attraction;
import com.parque.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class AttractionServiceImpl implements AttractionService {

    private final AttractionRepository attractionRepository;

    public AttractionServiceImpl(AttractionRepository attractionRepository) {
        this.attractionRepository = attractionRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<AttractionResponse> getAll() {
        return attractionRepository.findAll().stream().map(this::toResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public AttractionResponse getById(Long id) {
        Attraction attraction = attractionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Attraction not found"));
        return toResponse(attraction);
    }

    @Override
    public AttractionResponse create(AttractionCreateRequest request) {
        int maintenanceFrequencyDays = calculateMaintenanceFrequencyDays(request.size());

        Attraction attraction = Attraction.builder()
                .name(request.name())
                .description(request.description())
                .size(request.size())
                .status(request.status())
                .totalSeats(request.totalSeats())
                .availableSeats(request.availableSeats())
                .maintenanceFrequencyDays(maintenanceFrequencyDays)
                .imageUrl(request.imageUrl())
                .build();

        return toResponse(attractionRepository.save(attraction));
    }

    @Override
    public AttractionResponse update(Long id, AttractionUpdateRequest request) {
        Attraction attraction = attractionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Attraction not found"));

        int maintenanceFrequencyDays = calculateMaintenanceFrequencyDays(request.size());

        attraction.setName(request.name());
        attraction.setDescription(request.description());
        attraction.setSize(request.size());
        attraction.setStatus(request.status());
        attraction.setTotalSeats(request.totalSeats());
        attraction.setAvailableSeats(request.availableSeats());
        attraction.setMaintenanceFrequencyDays(maintenanceFrequencyDays);
        attraction.setImageUrl(request.imageUrl());

        return toResponse(attractionRepository.save(attraction));
    }

    @Override
    public void delete(Long id) {
        if (!attractionRepository.existsById(id)) {
            throw new ResourceNotFoundException("Attraction not found");
        }
        attractionRepository.deleteById(id);
    }

    private int calculateMaintenanceFrequencyDays(String size) {
        if (size == null) {
            return 14;
        }
        return switch (size.toUpperCase()) {
            case "LARGE" -> 7;
            case "SMALL" -> 30;
            default -> 14;
        };
    }

    private AttractionResponse toResponse(Attraction attraction) {
        return new AttractionResponse(
                attraction.getId(),
                attraction.getName(),
                attraction.getDescription(),
                attraction.getSize(),
                attraction.getStatus(),
                attraction.getTotalSeats(),
                attraction.getAvailableSeats(),
                attraction.getMaintenanceFrequencyDays(),
                attraction.getImageUrl()
        );
    }
}

