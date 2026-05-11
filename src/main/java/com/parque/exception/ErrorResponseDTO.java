package com.parque.exception;

import java.time.LocalDateTime;

public record ErrorResponseDTO(
        int status,
        String error,
        String message,
        String path,
        LocalDateTime timestamp
) {
}
