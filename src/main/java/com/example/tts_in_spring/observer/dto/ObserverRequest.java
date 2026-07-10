package com.example.tts_in_spring.observer.dto;

import jakarta.validation.constraints.NotNull;

public record ObserverRequest(
        @NotNull Long tournamentId
) {}
