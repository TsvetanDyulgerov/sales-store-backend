package com.heamimont.salesstoreapi.exceptions;

public class BadCredentialsException extends RuntimeException {
    public BadCredentialsException(String message, Throwable cause) {
        super(message, cause);
    }
}
