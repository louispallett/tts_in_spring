package com.example.tts_in_spring.builder;

import com.example.tts_in_spring.model.Category;
import com.example.tts_in_spring.model.Tournament;

public class CategoryTestBuilder {
    private final Long id = 100L;
    private String name = "Mens Singles";
    private final boolean locked = false;
    private boolean doubles = false;
    private final Tournament tournament = TournamentTestBuilder.aTournament().build();

    public static CategoryTestBuilder aCategory() {
        return new CategoryTestBuilder();
    }

    public CategoryTestBuilder withName(String name) {
        this.name = name;
        return this;
    }

    public CategoryTestBuilder withDoubles(boolean isDoubles) {
        this.doubles = isDoubles;
        return this;
    }

    public Category build() {
        Category category = new Category();
        category.setId(id);
        category.setName(name);
        category.setLocked(locked);
        category.setDoubles(doubles);
        category.setTournament(tournament);

        return category;
    }
}
