package com.example.tts_in_spring.dto.User;

import com.example.tts_in_spring.dto.Tournament.TournamentResponse;

import java.util.List;

public record UserResponse(
        Long id,
        String firstName,
        String lastName,
        String email,
        String mobCode,
        String mobile,
        List<TournamentResponse> tournaments
) {}