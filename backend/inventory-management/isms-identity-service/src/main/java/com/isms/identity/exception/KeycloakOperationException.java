package com.isms.identity.exception;

public class KeycloakOperationException extends RuntimeException {

    public KeycloakOperationException(String message) {
        super(message);
    }

    public KeycloakOperationException(String message, Throwable cause) {
        super(message, cause);
    }
}