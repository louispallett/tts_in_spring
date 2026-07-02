package com.example.tts_in_spring.exception;

public class IllegalStageException extends RuntimeException {
    public IllegalStageException() {
        super("Illegal Stage exception");
    }
    public IllegalStageException(String message) {
        super(message);
    }
}
