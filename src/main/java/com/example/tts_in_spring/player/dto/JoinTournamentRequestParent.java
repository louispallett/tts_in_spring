package com.example.tts_in_spring.player.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record JoinTournamentRequestParent(
        @NotBlank String tournamentCode,
        @NotNull(message = "Male boolean cannot be null") boolean male,
        String mobCode,
        String mobile,
        @NotNull(message = "Category array cannot be null") List<Long> categories
) {}
