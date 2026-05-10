package com.parque.cloudinary.service;

public record CloudinaryUploadResult(
        String imageUrl,
        String publicId
) {
}
