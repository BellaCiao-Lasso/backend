package com.restaurante.model.domain.state;

import com.restaurante.exception.EstadoInvalidoException;
import com.restaurante.model.domain.EstadoPedido;
import com.restaurante.model.domain.Pedido;

/**
 * Patrón State con bloqueo por defecto (Deny-by-Default).
 * Todas las transiciones lanzan {@link EstadoInvalidoException} (422) salvo que el estado concreto las sobrescriba.
 */
public abstract class AbstractPedidoState implements PedidoState {

    protected String getNombreEstado() {
        return this.getClass().getSimpleName().replace("State", "").toUpperCase();
    }

    @Override
    public void iniciarPreparacion(Pedido pedido) {
        throw new EstadoInvalidoException("No se puede pasar a EN_PREPARACION desde el estado " + getNombreEstado());
    }

    @Override
    public void marcarListo(Pedido pedido) {
        throw new EstadoInvalidoException("No se puede pasar a LISTO desde el estado " + getNombreEstado());
    }

    @Override
    public void entregar(Pedido pedido) {
        throw new EstadoInvalidoException("No se puede pasar a ENTREGADO desde el estado " + getNombreEstado());
    }

    @Override
    public void cancelar(Pedido pedido) {
        throw new EstadoInvalidoException("No se puede CANCELAR desde el estado " + getNombreEstado());
    }

    @Override
    public void transicionarA(Pedido pedido, EstadoPedido nuevoEstado) {
        if (nuevoEstado == null) {
            throw new EstadoInvalidoException("El nuevo estado del pedido no puede ser nulo.");
        }
        switch (nuevoEstado) {
            case EN_PREPARACION -> iniciarPreparacion(pedido);
            case LISTO -> marcarListo(pedido);
            case ENTREGADO -> entregar(pedido);
            case CANCELADO -> cancelar(pedido);
            default -> throw new EstadoInvalidoException(
                    "No se puede transicionar a " + nuevoEstado + " desde el estado " + getNombreEstado());
        }
    }
}
