package com.example.tts_in_spring.builder;

import com.example.tts_in_spring.model.Category;
import com.example.tts_in_spring.model.Match;

import java.time.Instant;

public class MatchTestBuilder {
    private Long id = 100000L;
    private final String tournamentRoundText = "1";
    private final String state = "SCHEDULED";
    private Instant date = Instant.now();
    private final int updateNumber = 0;
    private final boolean qualifyingMatch = false;
    private final Category category = CategoryTestBuilder.aCategory().build();
    private Match nextMatch = null;

    public static MatchTestBuilder aMatch() {
        return new MatchTestBuilder();
    }

    public MatchTestBuilder withId(Long id) {
        this.id = id;
        return this;
    }

    public MatchTestBuilder withDate(Instant date) {
        this.date = date;
        return this;
    }

    public MatchTestBuilder withNextMatch(Match nextMatch) {
        this.nextMatch = nextMatch;
        return this;
    }

    public Match build() {
        Match match = new Match();
        match.setId(id);
        match.setTournamentRoundText(tournamentRoundText);
        match.setState(state);
        match.setDate(date);
        match.setUpdateNumber(updateNumber);
        match.setQualifyingMatch(qualifyingMatch);
        match.setCategory(category);
        match.setNextMatch(nextMatch);

        return match;
    }
}
