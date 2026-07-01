package com.example.tts_in_spring.exception;

public class GenericBadRequestException extends RuntimeException {
    public GenericBadRequestException() {
        super("Request failed for non-specific reason");
    }

    public GenericBadRequestException(String message) {
        super(message);
    }
}
