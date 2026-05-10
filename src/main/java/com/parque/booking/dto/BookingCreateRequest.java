package com.parque.booking.dto;

import com.parque.validation.AllowedValues;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.time.LocalDate;
import java.util.List;

public record BookingCreateRequest(
        @NotNull
        @Positive
        Long userId,
        @Positive
        Long offerId,
        @Positive
        Long hotelId,
        @NotBlank
        @AllowedValues({"HALF_BOARD", "FULL_BOARD"})
        String boardType,
        @NotNull
        LocalDate visitDate,
        @NotEmpty
        List<@Valid CompanionRequest> companions
) {
}
