package com.parque.shift;

import com.parque.employee.dto.EmployeeCreateRequest;
import com.parque.employee.repository.EmployeeRepository;
import com.parque.employee.service.EmployeeService;
import com.parque.entity.Shift;
import com.parque.exception.ConflictException;
import com.parque.shift.dto.ShiftGenerateRequest;
import com.parque.shift.repository.ShiftRepository;
import com.parque.shift.service.ShiftService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Transactional
class ShiftServiceTest {

    @Autowired
    private ShiftService shiftService;

    @Autowired
    private ShiftRepository shiftRepository;

    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private EmployeeRepository employeeRepository;

    @BeforeEach
    void setUp() {
        shiftRepository.deleteAll();
        employeeRepository.deleteAll();
    }

    @Test
    void generate_shouldThrowConflict_whenNotEnoughEmployees() {
        employeeService.create(new EmployeeCreateRequest("A", "A", "12345678A", "a@a.com", "TECHNICIAN", "MORNING", true));
        employeeService.create(new EmployeeCreateRequest("B", "B", "12345678B", "b@b.com", "TECHNICIAN", "MORNING", true));
        employeeService.create(new EmployeeCreateRequest("C", "C", "12345678C", "c@c.com", "TECHNICIAN", "MORNING", true));

        assertThatThrownBy(() -> shiftService.generate(new ShiftGenerateRequest(LocalDate.parse("2026-05-01"), LocalDate.parse("2026-05-31"))))
                .isInstanceOf(ConflictException.class)
                .hasMessage("Not enough employees to cover required shifts");
    }

    @Test
    void generate_shouldCreateShiftsIn15DayPeriods() {
        for (int i = 0; i < 3; i++) {
            employeeService.create(new EmployeeCreateRequest("Cleaner" + i, "X", "1000000" + i + "A", "c" + i + "@e.com", "CLEANER", "MORNING", true));
            employeeService.create(new EmployeeCreateRequest("Animator" + i, "X", "2000000" + i + "A", "a" + i + "@e.com", "ANIMATOR", "MORNING", true));
            employeeService.create(new EmployeeCreateRequest("Tech" + i, "X", "3000000" + i + "A", "t" + i + "@e.com", "TECHNICIAN", "MORNING", true));
        }

        shiftService.generate(new ShiftGenerateRequest(LocalDate.parse("2026-05-01"), LocalDate.parse("2026-05-31")));

        List<Shift> shifts = shiftRepository.findAll();
        assertThat(shifts).isNotEmpty();
        Set<LocalDate> startDates = shifts.stream().map(Shift::getStartDate).collect(java.util.stream.Collectors.toSet());
        assertThat(startDates).contains(LocalDate.parse("2026-05-01"), LocalDate.parse("2026-05-16"), LocalDate.parse("2026-05-31"));
    }
}

