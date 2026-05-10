package com.parque.cloudinary.service;

import com.parque.cloudinary.dto.ImageUploadResponse;
import org.springframework.web.multipart.MultipartFile;

public interface ImageService {
    ImageUploadResponse upload(MultipartFile file, String folder);
}

