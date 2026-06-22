package com.example.tts_in_spring.participant.dto;

import jakarta.validation.constraints.NotEmpty;

public record ParticipantUpdateStatusRequest (
    @NotEmpty(message = "Status cannot be empty")
    String status
) {}