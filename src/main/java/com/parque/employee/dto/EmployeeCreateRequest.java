package com.parque.employee.dto;

import com.parque.validation.AllowedValues;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record EmployeeCreateRequest(
        @NotBlank
        @Size(min = 1, max = 100)
        String firstName,
        @NotBlank
        @Size(min = 1, max = 100)
        String lastName,
        @NotBlank
        @Pattern(regexp = "^[0-9]{8}[A-Z]$")
        String dni,
        @NotBlank
        @Email
        String email,
        @NotBlank
        @AllowedValues({"CLEANER", "ANIMATOR", "TECHNICIAN"})
        String employeeType,
        @NotBlank
        @AllowedValues({"MORNING", "AFTERNOON"})
        String shift,
        @NotNull
        Boolean active
) {
}

