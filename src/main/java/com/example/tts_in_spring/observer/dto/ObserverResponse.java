package com.example.tts_in_spring.observer.dto;

import com.example.tts_in_spring.tournament.dto.TournamentResponseLite;
import com.example.tts_in_spring.user.dto.UserResponseLite;

public record ObserverResponse(
        Long id,
        String name,
        UserResponseLite user,
        TournamentResponseLite tournament
) {}
