package com.example.tts_in_spring.user;

import com.example.tts_in_spring.tournament.TournamentResponseLite;

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