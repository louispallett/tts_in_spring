package com.example.tts_in_spring.dto;

import com.example.tts_in_spring.model.Category;

import java.util.List;

public class CategoryResponse {
    public Long id;
    public String name;
    public Boolean locked;
    public Boolean doubles;
    public TournamentResponse tournament;
    public List<PlayerResponse> players;

    public CategoryResponse(Category category) {
        this.id = category.getId();
        this.name = category.getName();
        this.locked = category.isLocked();
        this.doubles = category.isDoubles();
    }
}
