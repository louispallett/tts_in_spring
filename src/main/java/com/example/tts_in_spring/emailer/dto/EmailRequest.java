package com.example.tts_in_spring.emailer.dto;

import jakarta.validation.constraints.NotBlank;

public record EmailRequest(
        @NotBlank String subject,
        @NotBlank String text
) {}
