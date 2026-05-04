package com.parque.attraction.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

import com.parque.maintenance.model.Maintenance;

@Entity
@Table(name = "attractions")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Attraction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    private String name;

    @NotBlank
    private String description;

    @NotBlank
    private String size;

    @NotBlank
    private String status;

    @NotNull
    @Positive
    private Integer totalSeats;

    @NotNull
    @PositiveOrZero
    private Integer availableSeats;

    @NotNull
    @Positive
    private Integer maintenanceFrequencyDays;

    @NotBlank
    private String imageUrl;

    @OneToMany(mappedBy = "attraction", cascade = CascadeType.REMOVE)
    private List<Maintenance> maintenances;
}
