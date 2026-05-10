package com.parque.cloudinary.dto;

public record ImageUploadResponse(
        String imageUrl,
        String publicId
) {
}

