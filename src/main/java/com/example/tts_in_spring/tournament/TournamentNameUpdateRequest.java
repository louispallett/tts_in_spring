package com.example.tts_in_spring.tournament;

import jakarta.validation.constraints.NotBlank;

public record TournamentNameUpdateRequest (
    @NotBlank(message = "Name is required")
    String name
) {}
