package com.parque.entity;

import com.parque.booking.model.Booking;
import com.parque.booking.model.TicketStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

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

    @NotBlank
    @Column(nullable = false, unique = true, length = 64)
    private String entryToken;

    @NotBlank
    @Column(nullable = false, unique = true, length = 64)
    private String mobileAccessToken;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TicketStatus status;

    private LocalDateTime usedAt;
}
