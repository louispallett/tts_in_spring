package com.example.tts_in_spring.player.dto;

public record PlayerResponseLite(
        Long id,
        String name,
        boolean male,
        boolean seeded,
        int rank
) {}
