package com.restaurante.exception;

public class MesaNotFoundException extends RecursoNoEncontradoException {
    public MesaNotFoundException(String message) {
        super(message);
    }
}
