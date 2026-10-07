package com.example.tts_in_spring.emailer.dto;

import jakarta.validation.constraints.NotBlank;

public record GenericHostEmail(
        @NotBlank String firstName,
        @NotBlank String tournamentName,
        @NotBlank String to,
        @NotBlank String from
) {}
