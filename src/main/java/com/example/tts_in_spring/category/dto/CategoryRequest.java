package com.example.tts_in_spring.category.dto;

import com.example.tts_in_spring.category.Type;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CategoryRequest (
    @NotBlank(message = "Name is required")
    Type name,
    @NotNull(message = "tournamentId must not be null")
    Long tournamentId
) {}
