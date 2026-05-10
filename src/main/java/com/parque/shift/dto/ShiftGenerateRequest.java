package com.parque.shift.dto;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record ShiftGenerateRequest(
        @NotNull
        LocalDate startDate,
        @NotNull
        LocalDate endDate
) {
}

