package com.example.tts_in_spring.exception;

public class UnauthorizedException extends RuntimeException {
    public UnauthorizedException() {
        super("Unauthorized Request");
    }

    public UnauthorizedException(String message) {
        super(message);
    }
}
