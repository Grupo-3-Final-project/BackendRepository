package com.parque.offer.dto;

import java.math.BigDecimal;

public record OfferResponse(
        Long id,
        String title,
        String description,
        Long hotelId,
        String hotelName,
        String boardType,
        Integer includedTickets,
        BigDecimal totalPrice,
        String imageUrl
) {
}

