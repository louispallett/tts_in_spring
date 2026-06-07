package com.example.tts_in_spring.dto;

import com.example.tts_in_spring.model.Team;

import java.util.List;

public class TeamResponse {
    public Long id;
    public CategoryResponse category;
    public List<PlayerResponse> players;
    public List<ParticipantResponse> participants;

    public TeamResponse(Team team) {
        this.id = team.getId();
    }
}
