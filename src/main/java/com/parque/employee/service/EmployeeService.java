package com.parque.employee.service;

import com.parque.employee.dto.EmployeeCreateRequest;
import com.parque.employee.dto.EmployeeResponse;
import com.parque.employee.dto.EmployeeUpdateRequest;

import java.util.List;

public interface EmployeeService {
    List<EmployeeResponse> getAll();

    EmployeeResponse getById(Long id);

    EmployeeResponse create(EmployeeCreateRequest request);

    EmployeeResponse update(Long id, EmployeeUpdateRequest request);

    void delete(Long id);
}

