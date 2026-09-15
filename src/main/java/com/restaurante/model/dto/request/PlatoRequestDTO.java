package com.restaurante.model.dto.request;

import lombok.Data;

@Data
public class PlatoRequestDTO {
    private String nombre;
    private Double precio;
    private String categoria;
    private Boolean disponible;
}
