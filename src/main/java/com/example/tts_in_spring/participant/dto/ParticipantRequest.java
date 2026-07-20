package com.example.tts_in_spring.participant.dto;

import jakarta.validation.constraints.NotNull;

public record ParticipantRequest (
    Long teamId,
    Long playerId,
    @NotNull(message = "MatchId cannot be null")
    Long matchId
) {}
