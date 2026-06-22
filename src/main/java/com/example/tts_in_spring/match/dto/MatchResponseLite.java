package com.example.tts_in_spring.match.dto;

import java.time.Instant;

public record MatchResponseLite(
        Long id,
        String tournamentRoundText,
        String state,
        Instant deadline,
        boolean qualifyingMatch
) {}
