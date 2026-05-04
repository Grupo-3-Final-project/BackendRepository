package com.parque.maintenance.service;

import com.parque.attraction.model.Attraction;
import com.parque.attraction.repository.AttractionRepository;
import com.parque.employee.model.Employee;
import com.parque.employee.repository.EmployeeRepository;
import com.parque.exception.ConflictException;
import com.parque.maintenance.dto.MaintenanceGenerateRequest;
import com.parque.maintenance.dto.MaintenanceGenerateResponse;
import com.parque.maintenance.dto.MaintenanceResponse;
import com.parque.maintenance.dto.MaintenanceTechnicianResponse;
import com.parque.maintenance.model.Maintenance;
import com.parque.maintenance.repository.MaintenanceRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class MaintenanceServiceImpl implements MaintenanceService {

    private final MaintenanceRepository maintenanceRepository;
    private final AttractionRepository attractionRepository;
    private final EmployeeRepository employeeRepository;

    public MaintenanceServiceImpl(
            MaintenanceRepository maintenanceRepository,
            AttractionRepository attractionRepository,
            EmployeeRepository employeeRepository
    ) {
        this.maintenanceRepository = maintenanceRepository;
        this.attractionRepository = attractionRepository;
        this.employeeRepository = employeeRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<MaintenanceResponse> getAll() {
        return maintenanceRepository.findAll().stream().map(this::toResponse).toList();
    }

    @Override
    public MaintenanceGenerateResponse generate(MaintenanceGenerateRequest request) {
        List<Employee> technicians = employeeRepository.findAll().stream()
                .filter(Employee::getActive)
                .filter(e -> "TECHNICIAN".equalsIgnoreCase(e.getEmployeeType()))
                .toList();

        if (technicians.isEmpty()) {
            throw new ConflictException("Not enough technicians available");
        }

        List<Attraction> attractions = attractionRepository.findAll();
        LocalDate startDate = request.startDate();
        LocalDate endDate = request.endDate();

        List<Maintenance> tasks = new ArrayList<>();
        int technicianIndex = 0;
        for (Attraction attraction : attractions) {
            int frequencyDays = attraction.getMaintenanceFrequencyDays() == null ? 14 : attraction.getMaintenanceFrequencyDays();
            LocalDate date = startDate;
            while (!date.isAfter(endDate)) {
                Employee assigned = technicians.get(technicianIndex % technicians.size());
                technicianIndex++;

                tasks.add(Maintenance.builder()
                        .attraction(attraction)
                        .scheduledDate(date)
                        .status("SCHEDULED")
                        .technicians(List.of(assigned))
                        .build());

                date = date.plusDays(frequencyDays);
            }
        }

        maintenanceRepository.saveAll(tasks);
        return new MaintenanceGenerateResponse(
                "Maintenance schedule generated successfully",
                startDate,
                endDate,
                tasks.size()
        );
    }

    private MaintenanceResponse toResponse(Maintenance maintenance) {
        Attraction attraction = maintenance.getAttraction();
        List<MaintenanceTechnicianResponse> technicians = maintenance.getTechnicians() == null
                ? List.of()
                : maintenance.getTechnicians().stream()
                .map(t -> new MaintenanceTechnicianResponse(t.getId(), t.getFirstName() + " " + t.getLastName()))
                .toList();

        return new MaintenanceResponse(
                maintenance.getId(),
                attraction.getId(),
                attraction.getName(),
                maintenance.getScheduledDate(),
                maintenance.getStatus(),
                technicians
        );
    }
}

