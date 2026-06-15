package com.example.tts_in_spring.match;

import com.example.tts_in_spring.category.CategoryResponseLite;
import com.example.tts_in_spring.participant.ParticipantResponseLite;

import java.time.Instant;
import java.util.List;

public record MatchResponse (
        Long id,
        String tournamentRoundText,
        String state,
        Instant date,
        int updateNumber,
        boolean qualifyingMatch,
        CategoryResponseLite category,
        MatchResponseLite nextMatch,
        List<MatchResponseLite> previousMatches,
        List<ParticipantResponseLite> participants
) {}
