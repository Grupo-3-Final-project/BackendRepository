package com.parque.attraction.dto;

public record AttractionResponse(
        Long id,
        String name,
        String description,
        String size,
        String status,
        Integer totalSeats,
        Integer availableSeats,
        Integer maintenanceFrequencyDays,
        String imageUrl
) {
}

