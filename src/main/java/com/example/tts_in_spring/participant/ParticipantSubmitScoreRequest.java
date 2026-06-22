package com.example.tts_in_spring.participant;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ParticipantSubmitScoreRequest (
    @NotNull(message = "Participant id must not be null")
    Long id,

    @NotBlank(message = "Result Text must not be blank")
    String resultText,

    @NotNull(message = "isWinner boolean cannot be null")
    boolean isWinner
) {}
