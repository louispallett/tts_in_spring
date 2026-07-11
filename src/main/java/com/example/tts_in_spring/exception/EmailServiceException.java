package com.example.tts_in_spring.exception;

public class EmailServiceException extends RuntimeException {
    public EmailServiceException(String message) {
        super(message);
    }
}
