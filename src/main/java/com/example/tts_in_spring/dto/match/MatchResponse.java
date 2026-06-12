package com.example.tts_in_spring.dto.match;

import com.example.tts_in_spring.dto.category.CategoryResponseLite;
import com.example.tts_in_spring.dto.participant.ParticipantResponseLite;

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
