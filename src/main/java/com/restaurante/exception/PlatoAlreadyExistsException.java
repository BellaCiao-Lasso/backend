package com.restaurante.exception;

public class PlatoAlreadyExistsException extends ConflictoException {
    public PlatoAlreadyExistsException(String message) {
        super(message);
    }
}
