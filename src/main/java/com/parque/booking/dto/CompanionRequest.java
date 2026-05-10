package com.parque.booking.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record CompanionRequest(
        @NotBlank
        @Size(min = 1, max = 100)
        String firstName,
        @NotBlank
        @Size(min = 1, max = 100)
        String lastName,
        @NotNull
        @PastOrPresent
        LocalDate birthDate
) {
}
