package com.parque.employee;

import com.parque.employee.dto.EmployeeCreateRequest;
import com.parque.employee.dto.EmployeeResponse;
import com.parque.employee.dto.EmployeeUpdateRequest;
import com.parque.employee.repository.EmployeeRepository;
import com.parque.employee.service.EmployeeService;
import com.parque.exception.ConflictException;
import com.parque.exception.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Transactional
class EmployeeServiceTest {

    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private EmployeeRepository employeeRepository;

    @BeforeEach
    void setUp() {
        employeeRepository.deleteAll();
    }

    @Test
    void create_shouldCreateEmployee() {
        EmployeeCreateRequest request = new EmployeeCreateRequest(
                "Laura",
                "Gomez",
                "87654321B",
                "laura@example.com",
                "TECHNICIAN",
                "MORNING",
                true
        );

        EmployeeResponse created = employeeService.create(request);

        assertThat(created.id()).isNotNull();
        assertThat(created.dni()).isEqualTo("87654321B");
        assertThat(created.employeeType()).isEqualTo("TECHNICIAN");
    }

    @Test
    void create_shouldThrowConflict_whenEmailExists() {
        employeeService.create(new EmployeeCreateRequest(
                "Laura",
                "Gomez",
                "87654321B",
                "laura@example.com",
                "TECHNICIAN",
                "MORNING",
                true
        ));

        assertThatThrownBy(() -> employeeService.create(new EmployeeCreateRequest(
                "Ana",
                "Garcia",
                "12345678A",
                "laura@example.com",
                "CLEANER",
                "AFTERNOON",
                true
        ))).isInstanceOf(ConflictException.class).hasMessage("Email already exists");
    }

    @Test
    void update_shouldThrowNotFound_whenEmployeeDoesNotExist() {
        EmployeeUpdateRequest request = new EmployeeUpdateRequest(
                "Laura",
                "Gomez Perez",
                "87654321B",
                "laura@example.com",
                "TECHNICIAN",
                "AFTERNOON",
                true
        );

        assertThatThrownBy(() -> employeeService.update(999L, request))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Employee not found");
    }
}

