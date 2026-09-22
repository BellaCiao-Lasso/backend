package com.restaurante.model.dto.response;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ItemPedidoResponseDTO {
    private Long idPlato;
    private String nombrePlato;
    private Integer cantidad;
    private Double precioCongelado;
    private Double subtotal;
}
