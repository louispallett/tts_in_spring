package com.example.tts_in_spring.dto.user;

import com.example.tts_in_spring.dto.tournament.TournamentResponseLite;

import java.util.List;

public record UserResponse(
        Long id,
        String firstName,
        String lastName,
        String email,
        String mobCode,
        String mobile,
        List<TournamentResponseLite> tournaments
) {}