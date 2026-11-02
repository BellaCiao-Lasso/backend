package com.restaurante.model.domain.state;

import com.restaurante.exception.EstadoInvalidoException;
import com.restaurante.model.domain.EstadoPedido;

import java.util.EnumMap;
import java.util.Map;

public final class PedidoStateFactory {

    private static final Map<EstadoPedido, PedidoState> ESTADOS = new EnumMap<>(EstadoPedido.class);

    static {
        ESTADOS.put(EstadoPedido.RECIBIDO, new RecibidoState());
        ESTADOS.put(EstadoPedido.EN_PREPARACION, new EnPreparacionState());
        ESTADOS.put(EstadoPedido.LISTO, new ListoState());
        ESTADOS.put(EstadoPedido.ENTREGADO, new EntregadoState());
        ESTADOS.put(EstadoPedido.CANCELADO, new CanceladoState());
    }

    private PedidoStateFactory() {
    }

    public static PedidoState obtenerEstado(EstadoPedido estado) {
        if (estado == null || !ESTADOS.containsKey(estado)) {
            throw new EstadoInvalidoException("Estado de pedido desconocido: " + estado);
        }
        return ESTADOS.get(estado);
    }
}

