package com.restaurante.model.domain.state;

import com.restaurante.model.domain.EstadoPedido;
import com.restaurante.model.domain.Pedido;

public class EnPreparacionState extends AbstractPedidoState {

    @Override
    protected String getNombreEstado() {
        return EstadoPedido.EN_PREPARACION.name();
    }

    @Override
    public void marcarListo(Pedido pedido) {
        pedido.setEstado(EstadoPedido.LISTO);
    }

    @Override
    public void cancelar(Pedido pedido) {
        pedido.setEstado(EstadoPedido.CANCELADO);
    }
}
