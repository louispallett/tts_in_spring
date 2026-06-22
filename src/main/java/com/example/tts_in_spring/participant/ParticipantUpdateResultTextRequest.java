package com.example.tts_in_spring.participant;

import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ParticipantUpdateResultTextRequest {
    @NotEmpty(message = "Result text cannot be empty")
    private String resultText;
}