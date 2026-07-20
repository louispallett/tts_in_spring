package com.example.tts_in_spring.participant.dto;

import jakarta.validation.constraints.NotBlank;

public record ChangeWinningParticipantRequest(
        @NotBlank Long newWinningParticipant
) {}
