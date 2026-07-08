package com.example.tts_in_spring.participant.dto;

import com.example.tts_in_spring.match.dto.MatchResponseLite;
import com.example.tts_in_spring.player.dto.PlayerResponseLite;
import com.example.tts_in_spring.team.dto.TeamResponseLite;
import com.fasterxml.jackson.annotation.JsonProperty;

public record ParticipantResponse (
        Long id,
        String name,
        String resultText,
        @JsonProperty("winner") boolean winner,
        String status,
        TeamResponseLite team,
        PlayerResponseLite player,
        MatchResponseLite match
) {}
