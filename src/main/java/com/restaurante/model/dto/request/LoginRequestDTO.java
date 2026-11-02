package com.restaurante.model.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record LoginRequestDTO(
        @NotBlank(message = "El email es obligatorio")
        @Email(message = "El formato del email no es válido")
        @Schema(description = "Correo electrónico registrado", example = "admin@bellaciao.com")
        String email,

        @NotBlank(message = "La contraseña es obligatoria")
        @Schema(description = "Contraseña del usuario", example = "Admin123!")
        String password
) {
}
