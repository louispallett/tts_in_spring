package com.example.tts_in_spring.dto.tournament;

public record TournamentResponseLite (
        Long id,
        String name,
        String stage,
        boolean showMobile
) {}
