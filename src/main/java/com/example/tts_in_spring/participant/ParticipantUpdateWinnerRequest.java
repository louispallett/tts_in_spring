package com.example.tts_in_spring.participant;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ParticipantUpdateWinnerRequest {
    @NotNull(message = "isWinner boolean cannot be null")
    boolean isWinner;
}