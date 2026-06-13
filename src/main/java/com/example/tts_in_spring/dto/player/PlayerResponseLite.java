package com.example.tts_in_spring.dto.player;

public record PlayerResponseLite(
        Long id,
        boolean male,
        boolean seeded,
        int rank
) {}
