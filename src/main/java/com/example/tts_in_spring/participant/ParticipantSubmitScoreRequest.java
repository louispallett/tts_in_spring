package com.example.tts_in_spring.participant;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ParticipantSubmitScoreRequest {
    @NotNull(message = "Participant id must not be null")
    private Long id;

    @NotBlank(message = "Result Text must not be blank")
    private String resultText;

    @NotNull(message = "isWinner boolean cannot be null")
    private boolean isWinner;
}
