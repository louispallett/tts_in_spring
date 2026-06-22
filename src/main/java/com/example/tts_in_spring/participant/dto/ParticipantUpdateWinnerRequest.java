package com.example.tts_in_spring.participant.dto;

import jakarta.validation.constraints.NotNull;

public record ParticipantUpdateWinnerRequest (
    @NotNull(message = "isWinner boolean cannot be null")
    boolean isWinner
) {}