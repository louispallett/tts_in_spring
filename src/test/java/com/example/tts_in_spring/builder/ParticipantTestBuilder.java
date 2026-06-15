package com.example.tts_in_spring.builder;

import com.example.tts_in_spring.model.Match;
import com.example.tts_in_spring.model.Participant;
import com.example.tts_in_spring.model.Player;
import com.example.tts_in_spring.model.Team;

public class ParticipantTestBuilder {
    private Long id = 1000000L;
    private final String resultText = "";
    private final boolean isWinner = false;
    private final String status = "";
    private Team team = null;
    private Player player = null;
    private Match match = MatchTestBuilder.aMatch().build();

    public static ParticipantTestBuilder aParticipant() {
        return new ParticipantTestBuilder();
    }

    public ParticipantTestBuilder withId(Long id) {
        this.id = id;
        return this;
    }

    public ParticipantTestBuilder withTeam(Team team) {
        this.team = team;
        return this;
    }

    public ParticipantTestBuilder withPlayer(Player player) {
        this.player = player;
        return this;
    }

    public ParticipantTestBuilder withMatch(Match match) {
        this.match = match;
        return this;
    }

    public Participant build() {
        Participant participant = new Participant();
        participant.setId(id);
        participant.setResultText(resultText);
        participant.setWinner(isWinner);
        participant.setStatus(status);
        participant.setTeam(team);
        participant.setPlayer(player);
        participant.setMatch(match);

        return participant;
    }
}
