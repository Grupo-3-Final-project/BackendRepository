package com.parque.maintenance.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

import com.parque.attraction.model.Attraction;
import com.parque.employee.model.Employee;

@Entity
@Table(name = "maintenance")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Maintenance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "attraction_id", nullable = false)
    private Attraction attraction;

    @NotNull
    private LocalDate scheduledDate;

    @NotBlank
    private String status;

    @ManyToMany
    @JoinTable(
        name = "maintenance_technicians",
        joinColumns = @JoinColumn(name = "maintenance_id"),
        inverseJoinColumns = @JoinColumn(name = "employee_id")
    )
    private List<Employee> technicians;
}
