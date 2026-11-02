package com.restaurante.exception;

public class MesaAlreadyExistsException extends ConflictoException {
    public MesaAlreadyExistsException(String message) {
        super(message);
    }
}
