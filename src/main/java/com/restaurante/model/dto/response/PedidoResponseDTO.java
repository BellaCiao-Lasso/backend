package com.restaurante.model.dto.response;

import lombok.Data;

@Data
public class PedidoResponseDTO {
    private Long id;
    private Long idMesa;
    private String estado;
}
