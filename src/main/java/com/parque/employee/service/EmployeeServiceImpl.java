package com.parque.employee.service;

import com.parque.employee.dto.EmployeeCreateRequest;
import com.parque.employee.dto.EmployeeResponse;
import com.parque.employee.dto.EmployeeUpdateRequest;
import com.parque.employee.repository.EmployeeRepository;
import com.parque.entity.Employee;
import com.parque.exception.ConflictException;
import com.parque.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class EmployeeServiceImpl implements EmployeeService {

    private final EmployeeRepository employeeRepository;

    public EmployeeServiceImpl(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<EmployeeResponse> getAll() {
        return employeeRepository.findAll().stream().map(this::toResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public EmployeeResponse getById(Long id) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found"));
        return toResponse(employee);
    }

    @Override
    public EmployeeResponse create(EmployeeCreateRequest request) {
        if (employeeRepository.existsByEmail(request.email())) {
            throw new ConflictException("Email already exists");
        }
        if (employeeRepository.existsByDni(request.dni())) {
            throw new ConflictException("DNI already exists");
        }

        String employeeType = normalize(request.employeeType());
        String shift = normalize(request.shift());

        Employee employee = Employee.builder()
                .firstName(request.firstName())
                .lastName(request.lastName())
                .dni(request.dni())
                .email(request.email())
                .employeeType(employeeType)
                .shift(shift)
                .active(request.active())
                .build();

        return toResponse(employeeRepository.save(employee));
    }

    @Override
    public EmployeeResponse update(Long id, EmployeeUpdateRequest request) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found"));

        employeeRepository.findByEmail(request.email())
                .filter(other -> !other.getId().equals(id))
                .ifPresent(other -> {
                    throw new ConflictException("Email already exists");
                });

        employeeRepository.findByDni(request.dni())
                .filter(other -> !other.getId().equals(id))
                .ifPresent(other -> {
                    throw new ConflictException("DNI already exists");
                });

        String employeeType = normalize(request.employeeType());
        String shift = normalize(request.shift());

        employee.setFirstName(request.firstName());
        employee.setLastName(request.lastName());
        employee.setDni(request.dni());
        employee.setEmail(request.email());
        employee.setEmployeeType(employeeType);
        employee.setShift(shift);
        employee.setActive(request.active());

        return toResponse(employeeRepository.save(employee));
    }

    @Override
    public void delete(Long id) {
        if (!employeeRepository.existsById(id)) {
            throw new ResourceNotFoundException("Employee not found");
        }
        employeeRepository.deleteById(id);
    }

    private EmployeeResponse toResponse(Employee employee) {
        return new EmployeeResponse(
                employee.getId(),
                employee.getFirstName(),
                employee.getLastName(),
                employee.getDni(),
                employee.getEmail(),
                employee.getEmployeeType(),
                employee.getShift(),
                employee.getActive()
        );
    }

    private String normalize(String value) {
        return value.trim().toUpperCase();
    }
}

