package com.example.tts_in_spring.player.dto;

import jakarta.validation.constraints.NotNull;

public record PlayerRequest (
    @NotNull(message = "Male boolean cannot be null")
    boolean male,
    @NotNull(message = "categoryId cannot be null")
    Long categoryId
) {}
