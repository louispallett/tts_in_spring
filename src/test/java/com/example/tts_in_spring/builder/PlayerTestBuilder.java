package com.example.tts_in_spring.builder;

import com.example.tts_in_spring.model.Category;
import com.example.tts_in_spring.model.Player;
import com.example.tts_in_spring.model.Team;
import com.example.tts_in_spring.model.User;

public class PlayerTestBuilder {
    private Long id = 1000L;
    private final boolean male = true;
    private final boolean seeded = false;
    private final int rank = 0;
    private User user = UserTestBuilder.aUser().build();
    private Category category = CategoryTestBuilder.aCategory().build();
    private Team team = null;

    public static PlayerTestBuilder aPlayer() {
        return new PlayerTestBuilder();
    }

    public PlayerTestBuilder withId(Long id) {
        this.id = id;
        return this;
    }

    public PlayerTestBuilder withUser(User user) {
        this.user = user;
        return this;
    }

    public PlayerTestBuilder withCategory(Category category) {
        this.category = category;
        return this;
    }

    public PlayerTestBuilder withTeam(Team team) {
        this.team = team;
        return this;
    }

    public Player build() {
        Player player = new Player();
        player.setId(id);
        player.setMale(male);
        player.setSeeded(seeded);
        player.setRank(rank);
        player.setUser(user);
        player.setCategory(category);
        player.setTeam(team);

        return player;
    }

}
