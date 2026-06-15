package com.example.tts_in_spring.category;

public record CategoryResponseLite(
        Long id,
        String name,
        boolean locked,
        boolean doubles
) {}