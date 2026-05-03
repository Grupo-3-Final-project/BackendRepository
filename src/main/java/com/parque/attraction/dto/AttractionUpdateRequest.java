package com.parque.attraction.dto;

import com.parque.validation.AllowedValues;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;

public record AttractionUpdateRequest(
        @NotBlank
        String name,
        @NotBlank
        String description,
        @NotBlank
        @AllowedValues({"SMALL", "MEDIUM", "LARGE"})
        String size,
        @NotBlank
        @AllowedValues({"OPEN", "CLOSED", "MAINTENANCE"})
        String status,
        @NotNull
        @Positive
        Integer totalSeats,
        @NotNull
        @PositiveOrZero
        Integer availableSeats,
        @NotBlank
        String imageUrl
) {
}

