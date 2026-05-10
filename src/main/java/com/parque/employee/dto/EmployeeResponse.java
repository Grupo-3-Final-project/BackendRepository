package com.parque.employee.dto;

public record EmployeeResponse(
        Long id,
        String firstName,
        String lastName,
        String dni,
        String email,
        String employeeType,
        String shift,
        Boolean active
) {
}

