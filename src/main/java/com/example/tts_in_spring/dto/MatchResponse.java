package com.example.tts_in_spring.dto;

import com.example.tts_in_spring.model.Category;
import com.example.tts_in_spring.model.Match;

import java.time.Instant;
import java.util.List;

public class MatchResponse {
    public Long id;
    public String tournamentRoundText;
    public String state;
    public Instant date;
    public int updateNumber;
    public boolean qualifyingMatch;
    public Category category;
    public Match nextMatch;
    public List<MatchResponse> previousMatches;
    public List<ParticipantResponse> participants;

    public MatchResponse(Match match) {
        this.id = match.getId();
        this.tournamentRoundText = match.getTournamentRoundText();
        this.state = match.getState();
        this.date = match.getDate();
        this.updateNumber = match.getUpdateNumber();
        this.qualifyingMatch = match.isQualifyingMatch();
    }
}
