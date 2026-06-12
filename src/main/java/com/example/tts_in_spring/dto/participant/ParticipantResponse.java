package com.example.tts_in_spring.dto.participant;

import com.example.tts_in_spring.dto.match.MatchResponseLite;
import com.example.tts_in_spring.dto.player.PlayerResponseLite;
import com.example.tts_in_spring.dto.team.TeamResponseLite;

public record ParticipantResponse (
        Long id,
        String resultText,
        boolean isWinner,
        String status,
        TeamResponseLite team,
        PlayerResponseLite player,
        MatchResponseLite match
) {}
