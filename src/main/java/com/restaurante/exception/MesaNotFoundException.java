package com.restaurante.exception;

public class MesaNotFoundException extends RuntimeException {
    public MesaNotFoundException(String message) {
        super(message);
    }
}
