package com.parque.shift.dto;

import java.time.LocalDate;

public record ShiftGenerateResponse(
        String message,
        LocalDate startDate,
        LocalDate endDate,
        int totalGeneratedShifts
) {
}

