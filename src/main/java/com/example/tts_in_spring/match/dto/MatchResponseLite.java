package com.example.tts_in_spring.match.dto;

import com.example.tts_in_spring.match.State;

import java.time.Instant;

public record MatchResponseLite(
        Long id,
        String tournamentRoundText,
        State state,
        Instant deadline,
        boolean qualifyingMatch
) {}
