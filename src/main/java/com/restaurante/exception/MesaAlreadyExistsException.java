package com.restaurante.exception;

public class MesaAlreadyExistsException extends RuntimeException {
    public MesaAlreadyExistsException(String message) {
        super(message);
    }
}
