package com.parque.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Table(name = "employees")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Employee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Size(min = 1, max = 100)
    private String firstName;

    @NotBlank
    @Size(min = 1, max = 100)
    private String lastName;

    @NotBlank
    @Pattern(regexp = "^[0-9]{8}[A-Z]$")
    @Column(unique = true)
    private String dni;

    @Email
    @NotBlank
    @Column(unique = true)
    private String email;

    @NotBlank
    private String employeeType;

    @NotBlank
    private String shift;

    @NotNull
    private Boolean active;

    @OneToMany(mappedBy = "employee", cascade = CascadeType.REMOVE)
    private List<Shift> shifts;

    @ManyToMany(mappedBy = "technicians")
    private List<Maintenance> maintenances;
}
