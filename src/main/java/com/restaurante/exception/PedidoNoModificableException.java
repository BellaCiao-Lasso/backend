package com.restaurante.exception;

public class PedidoNoModificableException extends RuntimeException {
    public PedidoNoModificableException(String message) {
        super(message);
    }
}
