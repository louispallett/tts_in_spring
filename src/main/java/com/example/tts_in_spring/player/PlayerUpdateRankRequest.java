package com.example.tts_in_spring.player;

import jakarta.validation.constraints.NotNull;

public record PlayerUpdateRankRequest (
    @NotNull(message = "Rank cannot be null")
    int rank
) {}
