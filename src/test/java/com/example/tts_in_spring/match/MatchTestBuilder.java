package com.example.tts_in_spring.match;

import com.example.tts_in_spring.category.Category;
import com.example.tts_in_spring.category.CategoryTestBuilder;

import java.time.Instant;

public class MatchTestBuilder {
    private Long id = 100000L;
    private final String tournamentRoundText = "1";
    private final String state = "SCHEDULED";
    private final Instant deadline = Instant.now();
    private final boolean qualifyingMatch = false;
    private Category category = CategoryTestBuilder.aCategory().build();
    private Match nextMatch = null;

    public static MatchTestBuilder aMatch() {
        return new MatchTestBuilder();
    }

    public MatchTestBuilder withId(Long id) {
        this.id = id;
        return this;
    }

    public MatchTestBuilder withCategory(Category category) {
        this.category = category;
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
        match.setDeadline(deadline);
        match.setQualifyingMatch(qualifyingMatch);
        match.setCategory(category);
        match.setNextMatch(nextMatch);

        return match;
    }
}
