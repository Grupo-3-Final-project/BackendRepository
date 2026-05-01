package com.parque.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Entity
@Table(name = "offers")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Offer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    private String title;

    @NotBlank
    private String description;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "hotel_id", nullable = false)
    private Hotel hotel;

    @NotBlank
    private String boardType;

    @NotNull
    @Positive
    private Integer includedTickets;

    @NotNull
    @Positive
    private BigDecimal totalPrice;

    @NotBlank
    private String imageUrl;

    @OneToMany(mappedBy = "offer", cascade = CascadeType.REMOVE)
    private List<Booking> bookings;
}
