package com.restaurante.exception;

public class PlatoAlreadyExistsException extends RuntimeException {
    public PlatoAlreadyExistsException(String message) {
        super(message);
    }
}
