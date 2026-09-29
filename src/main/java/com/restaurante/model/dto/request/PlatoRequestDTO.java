package com.restaurante.model.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record PlatoRequestDTO(

    @NotBlank(message = "El nombre del plato es obligatorio")
    @Size(min = 2, max = 80, message = "El nombre debe tener entre 2 y 80 caracteres")
    @Schema(description = "Nombre del plato", example = "Pizza Margherita")
    String nombre,

    @NotNull(message = "El precio es obligatorio")
    @DecimalMin(value = "0.01", message = "El precio debe ser mayor a cero")
    @Schema(description = "Precio del plato", example = "25000.0")
    Double precio,

    @NotBlank(message = "La categoría es obligatoria")
    @Pattern(
        regexp = "ENTRADA|PLATO_FUERTE|POSTRE|BEBIDA",
        message = "Categoría inválida. Opciones: ENTRADA, PLATO_FUERTE, POSTRE, BEBIDA"
    )
    @Schema(description = "Categoría del plato", example = "PLATO_FUERTE")
    String categoria,

    @Size(max = 500, message = "La descripción no puede superar 500 caracteres")
    @Schema(description = "Descripción del plato", example = "Masa madre, salsa pomodoro y queso mozzarella")
    String descripcion,

    @Schema(description = "Disponibilidad del plato en el menú", example = "true", defaultValue = "true")
    Boolean disponible
) {}
