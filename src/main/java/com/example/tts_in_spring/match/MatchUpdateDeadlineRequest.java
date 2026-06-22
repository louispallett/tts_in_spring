package com.example.tts_in_spring.match;

import jakarta.validation.constraints.NotNull;

import java.time.Instant;

public record MatchUpdateDeadlineRequest (
        @NotNull(message = "Deadline must not be null")
        Instant deadline
) {}
