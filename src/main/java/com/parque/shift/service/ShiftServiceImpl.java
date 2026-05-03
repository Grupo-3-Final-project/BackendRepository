package com.parque.shift.service;

import com.parque.employee.repository.EmployeeRepository;
import com.parque.entity.Employee;
import com.parque.entity.Shift;
import com.parque.exception.ConflictException;
import com.parque.shift.dto.ShiftGenerateRequest;
import com.parque.shift.dto.ShiftGenerateResponse;
import com.parque.shift.dto.ShiftResponse;
import com.parque.shift.repository.ShiftRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class ShiftServiceImpl implements ShiftService {

    private static final int ROTATION_DAYS = 15;

    private final ShiftRepository shiftRepository;
    private final EmployeeRepository employeeRepository;

    public ShiftServiceImpl(ShiftRepository shiftRepository, EmployeeRepository employeeRepository) {
        this.shiftRepository = shiftRepository;
        this.employeeRepository = employeeRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<ShiftResponse> getAll() {
        return shiftRepository.findAll().stream().map(this::toResponse).toList();
    }

    @Override
    public ShiftGenerateResponse generate(ShiftGenerateRequest request) {
        validateCoverage();

        LocalDate startDate = request.startDate();
        LocalDate endDate = request.endDate();

        List<Employee> employees = employeeRepository.findAll().stream()
                .filter(Employee::getActive)
                .toList();

        List<Shift> generated = new ArrayList<>();
        int periodIndex = 0;
        LocalDate periodStart = startDate;
        while (!periodStart.isAfter(endDate)) {
            LocalDate periodEnd = periodStart.plusDays(ROTATION_DAYS - 1L);
            if (periodEnd.isAfter(endDate)) {
                periodEnd = endDate;
            }

            Map<String, List<Employee>> byType = employees.stream()
                    .collect(java.util.stream.Collectors.groupingBy(e -> e.getEmployeeType() == null ? "" : e.getEmployeeType().toUpperCase()));

            for (List<Employee> group : byType.values()) {
                List<Employee> sorted = group.stream()
                        .sorted(java.util.Comparator.comparing(Employee::getId))
                        .toList();
                for (int i = 0; i < sorted.size(); i++) {
                    Employee employee = sorted.get(i);
                    String shift = shiftForIndex(i, periodIndex);
                    generated.add(Shift.builder()
                            .employee(employee)
                            .shift(shift)
                            .startDate(periodStart)
                            .endDate(periodEnd)
                            .build());
                }
            }

            periodIndex++;
            periodStart = periodEnd.plusDays(1);
        }

        shiftRepository.saveAll(generated);
        return new ShiftGenerateResponse(
                "Shifts generated successfully",
                startDate,
                endDate,
                generated.size()
        );
    }

    private void validateCoverage() {
        List<Employee> activeEmployees = employeeRepository.findAll().stream()
                .filter(Employee::getActive)
                .toList();

        Map<String, Long> countsByType = activeEmployees.stream()
                .collect(java.util.stream.Collectors.groupingBy(
                        employee -> employee.getEmployeeType() == null ? "" : employee.getEmployeeType().trim().toUpperCase(),
                        java.util.stream.Collectors.counting()
                ));

        List<String> requiredTypes = List.of("CLEANER", "ANIMATOR", "TECHNICIAN");
        boolean enough = requiredTypes.stream().allMatch(type -> countsByType.getOrDefault(type, 0L) >= 3L);
        if (!enough) {
            throw new ConflictException("Not enough employees to cover required shifts");
        }
    }

    private String shiftForIndex(int indexWithinType, int periodIndex) {
        boolean firstGroupMorning = periodIndex % 2 == 0;
        boolean isMorning = indexWithinType % 2 == 0 ? firstGroupMorning : !firstGroupMorning;
        return isMorning ? "MORNING" : "AFTERNOON";
    }

    private ShiftResponse toResponse(Shift shift) {
        Employee employee = shift.getEmployee();
        String fullName = employee.getFirstName() + " " + employee.getLastName();
        return new ShiftResponse(
                shift.getId(),
                employee.getId(),
                fullName,
                employee.getEmployeeType(),
                shift.getShift(),
                shift.getStartDate(),
                shift.getEndDate()
        );
    }
}
