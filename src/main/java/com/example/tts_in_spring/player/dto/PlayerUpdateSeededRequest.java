package com.example.tts_in_spring.player.dto;

import jakarta.validation.constraints.NotNull;

public record PlayerUpdateSeededRequest (
    @NotNull(message = "Player Seeded boolean cannot be null")
    boolean seeded
) {}
