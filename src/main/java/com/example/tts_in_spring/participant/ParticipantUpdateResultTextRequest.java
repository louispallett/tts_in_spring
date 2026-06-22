package com.example.tts_in_spring.participant;

import jakarta.validation.constraints.NotEmpty;

public record ParticipantUpdateResultTextRequest (
    @NotEmpty(message = "Result text cannot be empty")
    String resultText
) {}