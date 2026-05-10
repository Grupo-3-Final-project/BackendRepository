package com.parque.maintenance.dto;

import java.time.LocalDate;

public record MaintenanceGenerateResponse(
        String message,
        LocalDate startDate,
        LocalDate endDate,
        int totalMaintenanceTasks
) {
}

