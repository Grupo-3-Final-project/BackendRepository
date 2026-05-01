package com.parque.offer.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record OfferCreateRequest(
        @NotBlank String title,
        @NotBlank String description,
        @NotNull @Positive Long hotelId,
        @NotBlank String boardType,
        @NotNull @Positive Integer includedTickets,
        @NotNull @Positive BigDecimal totalPrice,
        @NotBlank String imageUrl
) {
}

