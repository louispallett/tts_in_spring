package com.example.tts_in_spring.tournament.dto;

public record TournamentResponseLite (
        Long id,
        String name,
        String stage,
        boolean showMobile
) {}
