package com.example.tts_in_spring.tournament.dto;

import java.time.Instant;

public record TournamentResponseLite (
        Long id,
        String name,
        String stage,
        boolean showMobile,
        Instant dateCreated
) {}
