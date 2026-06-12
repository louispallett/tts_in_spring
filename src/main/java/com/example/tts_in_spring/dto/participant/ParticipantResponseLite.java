package com.example.tts_in_spring.dto.participant;

public record ParticipantResponseLite(
        Long id,
        String resultText,
        boolean isWinner,
        String status
) {}
