package com.parque.offer.dto;

import com.parque.validation.AllowedValues;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record OfferUpdateRequest(
        @NotBlank String title,
        @NotBlank String description,
        @NotNull @Positive Long hotelId,
        @AllowedValues({"HALF_BOARD", "FULL_BOARD"})
        @NotBlank String boardType,
        @NotNull @Positive Integer includedTickets,
        @NotNull @Positive BigDecimal totalPrice,
        @NotBlank String imageUrl
) {
}
