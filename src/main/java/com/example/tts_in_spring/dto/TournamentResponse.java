package com.example.tts_in_spring.dto;

import com.example.tts_in_spring.model.Tournament;

import java.util.List;

public class TournamentResponse {
    public Long id;
    public String name;
    public String stage;
    public String code;
    public Boolean showMobile;
    public UserResponse host;
    public List<CategoryResponse> categories;

    public TournamentResponse(Tournament tournament) {
        this.id = tournament.getId();
        this.name = tournament.getName();
        this.stage = tournament.getStage();
        this.code = tournament.getCode();
        this.showMobile = tournament.isShowMobile();
    }
}
