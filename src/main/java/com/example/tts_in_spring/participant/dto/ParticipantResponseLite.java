package com.example.tts_in_spring.participant.dto;
import com.fasterxml.jackson.annotation.JsonProperty;

public record ParticipantResponseLite(
        Long id,
        String resultText,
        @JsonProperty("winner") boolean winner,
        String status,
        String name
) {}
