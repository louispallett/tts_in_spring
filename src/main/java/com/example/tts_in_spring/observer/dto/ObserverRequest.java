package com.example.tts_in_spring.observer.dto;

import jakarta.validation.constraints.NotEmpty;

public record ObserverRequest(
        @NotEmpty String tournamentCode
) {}
