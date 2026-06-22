package com.example.tts_in_spring.match.dto;

import jakarta.validation.constraints.NotNull;

public record MatchSubmitScoreRequest (
    @NotNull(message = "State must not be null")
    String state
) {}
