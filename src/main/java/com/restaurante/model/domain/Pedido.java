package com.restaurante.model.domain;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import java.time.LocalDateTime;
import java.util.List;
import java.util.ArrayList;

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
        if (puedeModificarse()) {
            this.items.add(item);
        }
    }
    public void cambiarEstado(EstadoPedido nuevoEstado) {
        this.estado = nuevoEstado;
    }
}
