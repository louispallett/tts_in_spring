package com.example.tts_in_spring.tournament.dto;

import jakarta.validation.constraints.NotBlank;

public record TournamentCheckCodeRequest(
    @NotBlank(message = "Code is required")
    String code
) {}
