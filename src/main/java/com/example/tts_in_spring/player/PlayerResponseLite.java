package com.example.tts_in_spring.player;

public record PlayerResponseLite(
        Long id,
        boolean male,
        boolean seeded,
        int rank
) {}
