package com.example.tts_in_spring.tournament;

public record TournamentResponseLite (
        Long id,
        String name,
        String stage,
        boolean showMobile
) {}
