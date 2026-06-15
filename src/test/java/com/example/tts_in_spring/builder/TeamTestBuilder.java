package com.example.tts_in_spring.builder;

import com.example.tts_in_spring.model.Category;
import com.example.tts_in_spring.model.Team;

public class TeamTestBuilder {
    private Long id = 10000L;
    private Category category = CategoryTestBuilder.aCategory().build();

    public static TeamTestBuilder aTeam() {
        return new TeamTestBuilder();
    }

    public TeamTestBuilder withId(Long id) {
        this.id = id;
        return this;
    }

    public TeamTestBuilder withCategory(Category category) {
        this.category = category;
        return this;
    }

    public Team build() {
        Team team = new Team();
        team.setId(id);
        team.setCategory(category);

        return team;
    }
}
