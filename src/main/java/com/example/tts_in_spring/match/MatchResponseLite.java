package com.example.tts_in_spring.match;

import java.time.Instant;

public record MatchResponseLite(
        Long id,
        String tournamentRoundText,
        String state,
        Instant date,
        int updateNumber,
        boolean qualifyingMatch
) {}
