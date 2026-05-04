package com.parque.maintenance.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.parque.maintenance.model.Maintenance;

public interface MaintenanceRepository extends JpaRepository<Maintenance, Long> {
}

