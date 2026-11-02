package com.restaurante.model.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

public record AuthResponseDTO(
        @Schema(description = "Token JWT firmado", example = "eyJhbGciOiJIUzI1NiJ9...")
        String token
) {
}
