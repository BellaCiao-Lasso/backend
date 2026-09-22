package com.restaurante.model.dto.response;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MesaResponseDTO {
    private Long id;
    private Integer numero;
    private Integer capacidad;
    private String estado; // Vendrá de EstadoMesa enum
    private Boolean cuentaAbierta;
}
