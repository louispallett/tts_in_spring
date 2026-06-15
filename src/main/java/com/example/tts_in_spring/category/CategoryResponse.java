package com.example.tts_in_spring.category;

import com.example.tts_in_spring.match.MatchResponseLite;
import com.example.tts_in_spring.player.PlayerResponseLite;
import com.example.tts_in_spring.tournament.TournamentResponseLite;

import java.util.List;

public record CategoryResponse (
    Long id,
    String name,
    Boolean locked,
    Boolean doubles,
    TournamentResponseLite tournament,
    List<PlayerResponseLite> players,
    List<MatchResponseLite> matches
) {}
