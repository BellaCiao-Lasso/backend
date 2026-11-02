package com.restaurante.model.domain;

import com.restaurante.exception.PedidoNoModificableException;
import com.restaurante.model.domain.state.PedidoStateFactory;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Pedido {
    private Long id;
    private Long idMesa;
    @Builder.Default
    private List<ItemPedido> items = new ArrayList<>();
    private EstadoPedido estado;
    private LocalDateTime timestamp;

    public Boolean puedeModificarse() {
        return this.estado == EstadoPedido.RECIBIDO;
    }

    public void agregarItem(ItemPedido item) {
        if (!Boolean.TRUE.equals(puedeModificarse())) {
            throw new PedidoNoModificableException(
                    "No se pueden agregar ítems a un pedido en estado " + this.estado);
        }
        this.items.add(item);
    }

    public void cambiarEstado(EstadoPedido nuevoEstado) {
        if (this.estado == null || this.estado == nuevoEstado) {
            this.estado = nuevoEstado;
            return;
        }
        PedidoStateFactory.obtenerEstado(this.estado).transicionarA(this, nuevoEstado);
    }
}
