package com.example.tts_in_spring.participant.dto;

import jakarta.validation.constraints.NotNull;

public record ParticipantChangeMatchRequest(
        @NotNull Long oldParticipantId,
        Long playerId,
        Long teamId
) {}
