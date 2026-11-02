package com.restaurante.exception;

public class PedidoNotFoundException extends RecursoNoEncontradoException {
    public PedidoNotFoundException(String message) {
        super(message);
    }
}
