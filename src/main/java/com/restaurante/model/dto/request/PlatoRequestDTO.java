package com.restaurante.model.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record PlatoRequestDTO(

    @NotBlank(message = "El nombre del plato es obligatorio")
    @Size(min = 2, max = 80, message = "El nombre debe tener entre 2 y 80 caracteres")
    String nombre,

    @NotNull(message = "El precio es obligatorio")
    @DecimalMin(value = "0.01", message = "El precio debe ser mayor a cero")
    Double precio,

    @NotBlank(message = "La categoría es obligatoria")
    @Pattern(
        regexp = "ENTRADA|PLATO_FUERTE|POSTRE|BEBIDA",
        message = "Categoría inválida. Opciones: ENTRADA, PLATO_FUERTE, POSTRE, BEBIDA"
    )
    String categoria,

    @Size(max = 500, message = "La descripción no puede superar 500 caracteres")
    String descripcion // no es @NotBlank, puede ser null/vacío
) {}
