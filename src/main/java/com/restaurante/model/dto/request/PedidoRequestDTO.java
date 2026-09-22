package com.restaurante.model.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.util.List;

@Data
public class PedidoRequestDTO {

    @NotNull(message = "El ID de la mesa es obligatorio")
    private Long idMesa;

    @NotEmpty(message = "El pedido debe tener al menos un item")
    @Valid
    private List<ItemPedidoRequestDTO> items;
}
