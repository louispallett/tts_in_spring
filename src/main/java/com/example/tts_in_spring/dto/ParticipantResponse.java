package com.example.tts_in_spring.dto;

import com.example.tts_in_spring.model.Participant;

public class ParticipantResponse {
    public Long id;
    public String resultText;
    public boolean isWinner;
    public String status;
    public TeamResponse team;
    public PlayerResponse player;
    public MatchResponse match;

    public ParticipantResponse(Participant participant) {
        this.id = participant.getId();
        this.resultText = participant.getResultText();
        this.isWinner = participant.isWinner();
        this.status = participant.getStatus();
    }
}
