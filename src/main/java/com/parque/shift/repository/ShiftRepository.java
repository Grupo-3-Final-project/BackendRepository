package com.parque.shift.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.parque.shift.model.Shift;

import java.time.LocalDate;
import java.util.List;

public interface ShiftRepository extends JpaRepository<Shift, Long> {
    List<Shift> findByStartDateGreaterThanEqualAndEndDateLessThanEqual(LocalDate startDate, LocalDate endDate);
}

