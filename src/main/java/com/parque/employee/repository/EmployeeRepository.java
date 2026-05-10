package com.parque.employee.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.parque.employee.model.Employee;

import java.util.Optional;

public interface EmployeeRepository extends JpaRepository<Employee, Long> {
    boolean existsByEmail(String email);

    boolean existsByDni(String dni);

    Optional<Employee> findByEmail(String email);

    Optional<Employee> findByDni(String dni);
}

