package com.example.tts_in_spring.match.dto;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.Instant;

public record DeadlineByRoundRequest(
        @NotBlank String tournamentRoundText,
        @NotNull @FutureOrPresent Instant deadline
) {}
