package com.parque.maintenance;

import com.parque.attraction.dto.AttractionCreateRequest;
import com.parque.attraction.repository.AttractionRepository;
import com.parque.attraction.service.AttractionService;
import com.parque.employee.dto.EmployeeCreateRequest;
import com.parque.employee.model.Employee;
import com.parque.employee.repository.EmployeeRepository;
import com.parque.employee.service.EmployeeService;
import com.parque.exception.ConflictException;
import com.parque.maintenance.dto.MaintenanceGenerateRequest;
import com.parque.maintenance.model.Maintenance;
import com.parque.maintenance.repository.MaintenanceRepository;
import com.parque.maintenance.service.MaintenanceService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Transactional
class MaintenanceServiceTest {

    @Autowired
    private MaintenanceService maintenanceService;

    @Autowired
    private MaintenanceRepository maintenanceRepository;

    @Autowired
    private AttractionService attractionService;

    @Autowired
    private AttractionRepository attractionRepository;

    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private EmployeeRepository employeeRepository;

    @BeforeEach
    void setUp() {
        maintenanceRepository.deleteAll();
        attractionRepository.deleteAll();
        employeeRepository.deleteAll();
    }

    @Test
    void generate_shouldThrowConflict_whenNoTechnicians() {
        attractionService.create(new AttractionCreateRequest("A", "D", "LARGE", "OPEN", 10, 10, "https://example.com/a.jpg"));

        assertThatThrownBy(() -> maintenanceService.generate(new MaintenanceGenerateRequest(LocalDate.parse("2026-05-01"), LocalDate.parse("2026-05-31"))))
                .isInstanceOf(ConflictException.class)
                .hasMessage("Not enough technicians available");
    }

    @Test
    void generate_shouldCreateTasks_usingAttractionFrequency() {
        attractionService.create(new AttractionCreateRequest("A", "D", "LARGE", "OPEN", 10, 10, "https://example.com/a.jpg"));
        employeeService.create(new EmployeeCreateRequest("T", "T", "87654321B", "t@example.com", "TECHNICIAN", "MORNING", true));

        var response = maintenanceService.generate(new MaintenanceGenerateRequest(LocalDate.parse("2026-05-01"), LocalDate.parse("2026-05-31")));
        assertThat(response.totalMaintenanceTasks()).isGreaterThan(0);
        assertThat(maintenanceRepository.findAll()).isNotEmpty();
    }

    @Test
    void getAll_shouldMapMaintenanceAndTechnicians() {
        var attraction = attractionRepository.save(com.parque.attraction.model.Attraction.builder()
                .name("Dragon Coaster")
                .description("D")
                .size("LARGE")
                .status("OPEN")
                .totalSeats(30)
                .availableSeats(30)
                .maintenanceFrequencyDays(7)
                .imageUrl("https://example.com/a.jpg")
                .build());

        var technician = employeeRepository.save(Employee.builder()
                .firstName("Ana")
                .lastName("Tech")
                .dni("87654321Z")
                .email("ana.tech@example.com")
                .employeeType("TECHNICIAN")
                .shift("MORNING")
                .active(true)
                .build());

        maintenanceRepository.save(Maintenance.builder()
                .attraction(attraction)
                .scheduledDate(LocalDate.parse("2026-05-03"))
                .status("SCHEDULED")
                .technicians(java.util.List.of(technician))
                .build());

        var response = maintenanceService.getAll();

        assertThat(response).hasSize(1);
        assertThat(response.getFirst().attractionName()).isEqualTo("Dragon Coaster");
        assertThat(response.getFirst().technicians()).hasSize(1);
        assertThat(response.getFirst().technicians().getFirst().fullName()).isEqualTo("Ana Tech");
    }

    @Test
    void getAll_shouldReturnEmptyTechnicians_whenMaintenanceHasNone() {
        var attraction = attractionRepository.save(com.parque.attraction.model.Attraction.builder()
                .name("Splash River")
                .description("D")
                .size("MEDIUM")
                .status("OPEN")
                .totalSeats(20)
                .availableSeats(20)
                .maintenanceFrequencyDays(10)
                .imageUrl("https://example.com/b.jpg")
                .build());

        maintenanceRepository.save(Maintenance.builder()
                .attraction(attraction)
                .scheduledDate(LocalDate.parse("2026-05-08"))
                .status("SCHEDULED")
                .technicians(null)
                .build());

        var response = maintenanceService.getAll();

        assertThat(response).hasSize(1);
        assertThat(response.getFirst().technicians()).isEmpty();
    }
}

