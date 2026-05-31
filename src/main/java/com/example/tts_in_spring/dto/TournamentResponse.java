package com.example.tts_in_spring.dto;

import com.example.tts_in_spring.model.Tournament;

public class TournamentResponse {
    public Long id;
    public String name;
    public Tournament.Stage stage;
    public String code;
    public Boolean showMobile;
    public UserResponse host;

    public TournamentResponse(Tournament tournament) {
        this.id = tournament.getId();
        this.name = tournament.getName();
        this.stage = tournament.getStage();
        this.code = tournament.getCode();
        this.showMobile = tournament.getShowMobile();
    }
}
