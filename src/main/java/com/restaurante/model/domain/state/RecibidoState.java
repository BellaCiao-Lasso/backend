package com.restaurante.model.domain.state;

import com.restaurante.model.domain.EstadoPedido;
import com.restaurante.model.domain.Pedido;

public class RecibidoState extends AbstractPedidoState {

    @Override
    public void iniciarPreparacion(Pedido pedido) {
        pedido.setEstado(EstadoPedido.EN_PREPARACION);
    }

    @Override
    public void cancelar(Pedido pedido) {
        pedido.setEstado(EstadoPedido.CANCELADO);
    }
}

