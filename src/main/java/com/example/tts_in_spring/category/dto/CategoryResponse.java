package com.example.tts_in_spring.category.dto;

import com.example.tts_in_spring.match.dto.MatchResponseLite;
import com.example.tts_in_spring.player.dto.PlayerResponseLite;
import com.example.tts_in_spring.tournament.dto.TournamentResponseLite;

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
