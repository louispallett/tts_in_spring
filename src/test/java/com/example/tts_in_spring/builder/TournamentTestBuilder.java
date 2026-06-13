package com.example.tts_in_spring.builder;

import com.example.tts_in_spring.model.Tournament;
import com.example.tts_in_spring.model.User;

public class TournamentTestBuilder {
    private final Long id = 10L;
    private String name = "Test Tournament";
    private final String stage = "SIGN_UP";
    private final User host = UserTestBuilder.aUser().build();
    private final String code = "1234567";
    private boolean showMobile = false;

    public static TournamentTestBuilder aTournament() {
        return new TournamentTestBuilder();
    }

    public TournamentTestBuilder withName(String name) {
        this.name = name;
        return this;
    }

    public TournamentTestBuilder withShowMobile(boolean showMobile) {
        this.showMobile = showMobile;
        return this;
    }

    public Tournament build() {
        Tournament tournament = new Tournament();
        tournament.setId(id);
        tournament.setName(name);
        tournament.setStage(stage);
        tournament.setHost(host);
        tournament.setCode(code);
        tournament.setShowMobile(showMobile);

        return tournament;
    }
}
