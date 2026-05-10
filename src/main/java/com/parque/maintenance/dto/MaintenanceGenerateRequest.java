package com.parque.maintenance.dto;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record MaintenanceGenerateRequest(
        @NotNull
        LocalDate startDate,
        @NotNull
        LocalDate endDate
) {
}

