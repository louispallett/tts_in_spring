package com.example.tts_in_spring.category;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CategoryRequest (
    @NotBlank(message = "Name is required")
    String name,
    @NotNull(message = "tournamentId must not be null")
    Long tournamentId
) {}
