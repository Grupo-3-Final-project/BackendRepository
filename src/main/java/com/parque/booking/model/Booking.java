package com.parque.booking.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import org.hibernate.annotations.UpdateTimestamp;

import com.parque.entity.Ticket;
import com.parque.hotel.model.Hotel;
import com.parque.offer.model.Offer;
import com.parque.user.model.User;

@Entity
@Table(name = "bookings")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "offer_id")
    private Offer offer;

    @ManyToOne
    @JoinColumn(name = "hotel_id")
    private Hotel hotel;

    @NotBlank
    private String boardType;

    @NotNull
    private LocalDate visitDate;

    @OneToMany(mappedBy = "booking", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Ticket> tickets;

    @NotNull
    @Positive
    private BigDecimal totalPrice;

    @NotNull
    private Boolean emailSent;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
