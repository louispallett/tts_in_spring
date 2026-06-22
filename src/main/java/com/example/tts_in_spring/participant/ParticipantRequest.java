package com.example.tts_in_spring.participant;

import jakarta.validation.constraints.NotNull;

public record ParticipantRequest (
    Long teamId,
    Long playerId,
    @NotNull(message = "TeamId cannot be null")
    Long matchId
) {}
