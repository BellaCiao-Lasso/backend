package com.restaurante.model.domain.state;

import com.restaurante.model.domain.EstadoPedido;
import com.restaurante.model.domain.Pedido;

public class ListoState extends AbstractPedidoState {

    @Override
    public void entregar(Pedido pedido) {
        pedido.setEstado(EstadoPedido.ENTREGADO);
    }
}
