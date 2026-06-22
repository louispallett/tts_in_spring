package com.example.tts_in_spring.match;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.Instant;

public record MatchRequest (
    @NotNull(message = "Tournament Round Text cannot be null")
    @NotBlank(message = "Tournament Round Text cannot be blank")
    String tournamentRoundText,

    @NotNull(message = "Deadline cannot be null")
    Instant deadline,

    @NotNull(message = "Qualifying match boolean cannot be null")
    boolean qualifyingMatch,

    @NotNull(message = "Category cannot be null")
    Long categoryId,

    Long nextMatchId
) {}
