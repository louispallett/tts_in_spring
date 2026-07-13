package com.example.tts_in_spring.user.dto;

import com.example.tts_in_spring.player.dto.PlayerResponseLite;
import com.example.tts_in_spring.tournament.dto.TournamentResponseLite;

import java.util.List;

public record UserResponse(
        Long id,
        String firstName,
        String lastName,
        String email,
        List<TournamentResponseLite> tournaments,
        List<PlayerResponseLite> players
) {}