package com.example.tts_in_spring.match.dto;

import com.example.tts_in_spring.category.dto.CategoryResponseLite;
import com.example.tts_in_spring.match.State;
import com.example.tts_in_spring.participant.dto.ParticipantResponseLite;
import com.example.tts_in_spring.score.dto.ScoreResponse;

import java.time.Instant;
import java.util.List;

public record MatchResponse (
        Long id,
        String tournamentRoundText,
        State state,
        Instant deadline,
        boolean qualifyingMatch,
        CategoryResponseLite category,
        MatchResponseLite nextMatch,
        ScoreResponse score,
        List<MatchResponseLite> previousMatches,
        List<ParticipantResponseLite> participants
) {}
