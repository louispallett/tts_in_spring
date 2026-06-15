package com.example.tts_in_spring.builder;

import com.example.tts_in_spring.model.Tournament;
import com.example.tts_in_spring.model.User;

public class TournamentTestBuilder {
    private Long id = 10L;
    private final String name = "Test Tournament";
    private final String stage = "SIGN_UP";
    private User host = UserTestBuilder.aUser().build();
    private final String code = "1234567";
    private final boolean showMobile = false;

    public static TournamentTestBuilder aTournament() {
        return new TournamentTestBuilder();
    }

    public TournamentTestBuilder withId(Long id) {
        this.id = id;
        return this;
    }

    public TournamentTestBuilder withHost(User host) {
        this.host = host;
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
