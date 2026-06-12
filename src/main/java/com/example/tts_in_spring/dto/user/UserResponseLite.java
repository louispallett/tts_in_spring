package com.example.tts_in_spring.dto.user;

public record UserResponseLite(
        Long id,
        String firstName,
        String lastName
) {}
