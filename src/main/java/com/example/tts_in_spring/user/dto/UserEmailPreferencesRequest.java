package com.example.tts_in_spring.user.dto;

import jakarta.validation.constraints.NotNull;

public record UserEmailPreferencesRequest(
        @NotNull boolean emailPreference
) {}
