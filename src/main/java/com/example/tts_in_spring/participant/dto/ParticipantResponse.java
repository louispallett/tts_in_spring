package com.example.tts_in_spring.participant.dto;

import com.example.tts_in_spring.match.dto.MatchResponseLite;
import com.example.tts_in_spring.player.dto.PlayerResponseLite;
import com.example.tts_in_spring.team.dto.TeamResponseLite;

public record ParticipantResponse (
        Long id,
        String resultText,
        boolean isWinner,
        String status,
        TeamResponseLite team,
        PlayerResponseLite player,
        MatchResponseLite match
) {}
