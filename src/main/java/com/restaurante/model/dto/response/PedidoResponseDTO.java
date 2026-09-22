package com.restaurante.model.dto.response;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class PedidoResponseDTO {
    private Long id;
    private Long idMesa;
    private String estado;
    private LocalDateTime timestamp;
    private List<ItemPedidoResponseDTO> items;
    
    public Double getTotal() {
        if (items == null) return 0.0;
        return items.stream()
                .mapToDouble(ItemPedidoResponseDTO::getSubtotal)
                .sum();
    }
}
