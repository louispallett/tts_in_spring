package com.example.tts_in_spring.exception;

public class ForbiddenException extends RuntimeException {
    public ForbiddenException() {
        super("You are not authorized to access this resource");
    }

    public ForbiddenException(String message) {
        super(message);
    }
}