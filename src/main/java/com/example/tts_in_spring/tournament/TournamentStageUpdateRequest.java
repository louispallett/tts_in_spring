package com.example.tts_in_spring.tournament;

import jakarta.validation.constraints.NotBlank;

public record TournamentStageUpdateRequest (
    @NotBlank(message = "Stage is required")
    String stage
) {}
