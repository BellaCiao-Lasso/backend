package com.restaurante.exception;

public class PlatoNotFoundException extends RecursoNoEncontradoException {
    public PlatoNotFoundException(String message) {
        super(message);
    }
}
