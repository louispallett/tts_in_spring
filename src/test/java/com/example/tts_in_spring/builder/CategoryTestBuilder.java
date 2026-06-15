package com.example.tts_in_spring.builder;

import com.example.tts_in_spring.model.Category;
import com.example.tts_in_spring.model.Tournament;

public class CategoryTestBuilder {
    private Long id = 100L;
    private final String name = "Mens Singles";
    private final boolean locked = false;
    private final boolean doubles = false;
    private Tournament tournament = TournamentTestBuilder.aTournament().build();

    public static CategoryTestBuilder aCategory() {
        return new CategoryTestBuilder();
    }

    public CategoryTestBuilder withId(Long id) {
        this.id = id;
        return this;
    }

    public CategoryTestBuilder withTournament(Tournament tournament) {
        this.tournament = tournament;
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
