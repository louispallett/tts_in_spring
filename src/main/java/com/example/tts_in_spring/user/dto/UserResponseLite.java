package com.example.tts_in_spring.user.dto;

public record UserResponseLite(
        Long id,
        String firstName,
        String lastName
) {}
