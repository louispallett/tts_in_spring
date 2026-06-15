package com.example.tts_in_spring.participant;

import com.example.tts_in_spring.match.MatchResponseLite;
import com.example.tts_in_spring.player.PlayerResponseLite;
import com.example.tts_in_spring.team.TeamResponseLite;

public record ParticipantResponse (
        Long id,
        String resultText,
        boolean isWinner,
        String status,
        TeamResponseLite team,
        PlayerResponseLite player,
        MatchResponseLite match
) {}
