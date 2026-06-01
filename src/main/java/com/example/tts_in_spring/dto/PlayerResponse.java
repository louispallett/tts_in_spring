package com.example.tts_in_spring.dto;

import com.example.tts_in_spring.model.Player;

public class PlayerResponse {
    public Long id;
    public Boolean male;
    public Boolean seeded;
    public int rank;
    public UserResponse user;
    public TournamentResponse tournament;
    public CategoryResponse category;

    public PlayerResponse(Player player) {
        this.id = player.getId();
        this.male = player.getMale();
        this.seeded = player.getSeeded();
        this.rank = player.getRank();
    }
}
