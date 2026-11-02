package com.restaurante.model.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegistroRequestDTO(
        @NotBlank(message = "El email es obligatorio")
        @Email(message = "El formato del email no es válido")
        @Schema(description = "Correo electrónico del usuario", example = "chef@bellaciao.com")
        String email,

        @NotBlank(message = "La contraseña es obligatoria")
        @Size(min = 6, max = 100, message = "La contraseña debe tener entre 6 y 100 caracteres")
        @Schema(description = "Contraseña del usuario", example = "Secreta123!")
        String password,

        @NotBlank(message = "El rol es obligatorio")
        @Schema(description = "Rol del usuario (ADMIN, CHEF, MESERO, CAJERO, CLIENTE)", example = "MESERO")
        String rol
) {
}

