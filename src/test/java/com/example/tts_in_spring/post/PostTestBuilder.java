package com.example.tts_in_spring.post;

import com.example.tts_in_spring.tournament.Tournament;
import com.example.tts_in_spring.tournament.TournamentTestBuilder;

public class PostTestBuilder {
    private Long id = 10000000L;
    private String title = "Test title";
    private String content = "Test content for a test title.";
    private Tournament tournament = TournamentTestBuilder.aTournament().build();

    public static PostTestBuilder aPost() {
        return new PostTestBuilder();
    }

    public PostTestBuilder withTournament(Tournament tournament) {
        this.tournament = tournament;
        return this;
    }

    public Post build() {
        Post post = new Post();
        post.setId(id);
        post.setTitle(title);
        post.setContent(content);
        post.setTournament(tournament);

        return post;
    }
}
