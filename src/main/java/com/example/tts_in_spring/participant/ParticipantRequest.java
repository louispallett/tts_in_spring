package com.example.tts_in_spring.participant;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ParticipantRequest {
    private Long teamId;

    private Long playerId;

    @NotNull(message = "TeamId cannot be null")
    private Long matchId;
}
