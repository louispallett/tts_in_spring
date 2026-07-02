package com.example.tts_in_spring.player.dto;

import jakarta.validation.constraints.NotNull;

import java.util.List;

public record JoinTournamentRequest(
        @NotNull(message = "Male boolean cannot be null")
        boolean male,
        @NotNull(message = "Category array cannot be null")
        List<Long> categories
) {}
