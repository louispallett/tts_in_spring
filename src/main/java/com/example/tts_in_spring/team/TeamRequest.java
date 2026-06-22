package com.example.tts_in_spring.team;

import jakarta.validation.constraints.NotNull;

public record TeamRequest (
    @NotNull(message = "categoryId must not be null")
    Long categoryId
) {}
