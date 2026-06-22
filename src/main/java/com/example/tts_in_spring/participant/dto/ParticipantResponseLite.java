package com.example.tts_in_spring.participant.dto;

public record ParticipantResponseLite(
        Long id,
        String resultText,
        boolean isWinner,
        String status,
        String name
) {}
