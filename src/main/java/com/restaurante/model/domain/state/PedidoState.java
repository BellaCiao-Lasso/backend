package com.restaurante.model.domain.state;

import com.restaurante.model.domain.EstadoPedido;
import com.restaurante.model.domain.Pedido;

public interface PedidoState {
    void iniciarPreparacion(Pedido pedido);
    void marcarListo(Pedido pedido);
    void entregar(Pedido pedido);
    void cancelar(Pedido pedido);
    void transicionarA(Pedido pedido, EstadoPedido nuevoEstado);
}
