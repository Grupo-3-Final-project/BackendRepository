package com.parque.shift.dto;

import java.time.LocalDate;

public record ShiftResponse(
        Long id,
        Long employeeId,
        String employeeFullName,
        String employeeType,
        String shift,
        LocalDate startDate,
        LocalDate endDate
) {
}

