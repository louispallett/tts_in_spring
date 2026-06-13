package com.example.tts_in_spring.builder;

import com.example.tts_in_spring.model.Category;
import com.example.tts_in_spring.model.Player;
import com.example.tts_in_spring.model.Team;
import com.example.tts_in_spring.model.User;

public class PlayerTestBuilder {
    private final Long id = 1000L;
    private boolean male = true;
    private boolean seeded = false;
    private int rank = 0;
    private final User user = UserTestBuilder.aUser().build();
    private final Category category = CategoryTestBuilder.aCategory().build();
    private Team team = null;

    public static PlayerTestBuilder aPlayer() {
        return new PlayerTestBuilder();
    }

    public PlayerTestBuilder withMale(boolean male) {
        this.male = male;
        return this;
    }

    public PlayerTestBuilder withSeeded(boolean seeded) {
        this.seeded = seeded;
        return this;
    }

    public PlayerTestBuilder withRank(int rank) {
        this.rank = rank;
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
