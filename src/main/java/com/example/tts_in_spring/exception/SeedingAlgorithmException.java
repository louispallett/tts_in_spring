package com.example.tts_in_spring.exception;

public class SeedingAlgorithmException extends RuntimeException {
    public SeedingAlgorithmException() {
        super("Undefined seeding algorithm exception");
    }

    public SeedingAlgorithmException(String message) {
        super(message);
    }
}
