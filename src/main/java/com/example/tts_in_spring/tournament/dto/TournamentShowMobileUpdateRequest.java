package com.example.tts_in_spring.tournament.dto;

import jakarta.validation.constraints.NotNull;

public record TournamentShowMobileUpdateRequest (
    @NotNull(message = "Show Mobile boolean must not be null")
    boolean showMobile
) {}
