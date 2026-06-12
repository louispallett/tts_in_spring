package com.example.tts_in_spring.dto.category;

import com.example.tts_in_spring.dto.tournament.TournamentResponse;
import com.example.tts_in_spring.dto.player.PlayerResponseLite;

import java.util.List;

public record CategoryResponse (
    Long id,
    String name,
    Boolean locked,
    Boolean doubles,
    TournamentResponse tournament,
    List<PlayerResponseLite> players
) {}
