package com.restaurante.exception;

public class PlatoNotFoundException extends RuntimeException {
    public PlatoNotFoundException(String message) {
        super(message);
    }
}
