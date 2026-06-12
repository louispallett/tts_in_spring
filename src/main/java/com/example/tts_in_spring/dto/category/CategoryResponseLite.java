package com.example.tts_in_spring.dto.category;

public record CategoryResponseLite(
        Long id,
        String name,
        boolean locked,
        boolean doubles
) {}