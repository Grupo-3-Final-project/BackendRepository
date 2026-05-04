package com.parque.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

import com.parque.booking.model.Booking;

@Entity
@Table(name = "tickets")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Ticket {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "booking_id", nullable = false)
    private Booking booking;

    @NotBlank
    private String holderFullName;

    @NotBlank
    @Pattern(regexp = "^(CHILD|ADULT|SENIOR)$")
    private String ageRange;

    @NotNull
    @Positive
    private BigDecimal price;
}
