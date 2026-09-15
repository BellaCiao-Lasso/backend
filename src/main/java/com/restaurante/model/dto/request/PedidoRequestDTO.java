package com.restaurante.model.dto.request;

import lombok.Data;
import java.util.List;

@Data
public class PedidoRequestDTO {
    private Long idMesa;
    private List<Long> idsPlatos;
}
