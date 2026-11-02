package com.restaurante.exception;

public class PedidoNoModificableException extends ReglaDeNegocioException {
    public PedidoNoModificableException(String message) {
        super(message);
    }
}
