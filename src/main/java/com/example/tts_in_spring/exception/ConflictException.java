package com.example.tts_in_spring.exception;

public class ConflictException extends RuntimeException {
    public ConflictException() {
        super("Conflict in request");
    }

    public ConflictException(String message) {
        super(message);
    }
}
