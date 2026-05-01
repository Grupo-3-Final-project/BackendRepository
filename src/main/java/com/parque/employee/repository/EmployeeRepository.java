package com.parque.employee.repository;

import com.parque.entity.Employee;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EmployeeRepository extends JpaRepository<Employee, Long> {
    boolean existsByEmail(String email);

    boolean existsByDni(String dni);

    Optional<Employee> findByEmail(String email);

    Optional<Employee> findByDni(String dni);
}

