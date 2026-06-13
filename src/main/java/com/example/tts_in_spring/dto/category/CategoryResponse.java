package com.example.tts_in_spring.dto.category;

import com.example.tts_in_spring.dto.player.PlayerResponseLite;
import com.example.tts_in_spring.dto.tournament.TournamentResponseLite;

import java.util.List;

public record CategoryResponse (
    Long id,
    String name,
    Boolean locked,
    Boolean doubles,
    TournamentResponseLite tournament,
    List<PlayerResponseLite> players
) {}
