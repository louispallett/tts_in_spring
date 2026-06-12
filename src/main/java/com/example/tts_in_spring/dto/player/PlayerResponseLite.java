package com.example.tts_in_spring.dto.player;

public record PlayerResponseLite(
        Long id,
        String name,
        boolean locked,
        boolean doubles
) {}
