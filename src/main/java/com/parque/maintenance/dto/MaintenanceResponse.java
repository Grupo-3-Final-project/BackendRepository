package com.parque.maintenance.dto;

import java.time.LocalDate;
import java.util.List;

public record MaintenanceResponse(
        Long id,
        Long attractionId,
        String attractionName,
        LocalDate scheduledDate,
        String status,
        List<MaintenanceTechnicianResponse> technicians
) {
}

