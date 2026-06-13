package com.example.tts_in_spring.builder;

import com.example.tts_in_spring.model.Category;
import com.example.tts_in_spring.model.Team;

public class TeamTestBuilder {
    private final Long id = 10000L;
    private final Category category = CategoryTestBuilder.aCategory().build();

    public static TeamTestBuilder aTeam() {
        return new TeamTestBuilder();
    }

    public Team build() {
        Team team = new Team();
        team.setId(id);
        team.setCategory(category);

        return team;
    }
}
