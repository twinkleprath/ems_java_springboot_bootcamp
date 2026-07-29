package com.example.ems.exception;

public class DuplicateResourceException extends RuntimeException {
    public DuplicateResourceException() {
    }

    public DuplicateResourceException(String message) {
        super(message);
    }
}
