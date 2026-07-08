package com.example.tts_in_spring.participant.dto;

import jakarta.validation.constraints.NotNull;

public record ParticipantSubmitScoreRequest (
        @NotNull Long id,
        @NotNull String resultText,
        @NotNull boolean winner
) {}
