package com.example.tts_in_spring.category;

import jakarta.validation.constraints.NotNull;

public record CategoryLockedUpdateRequest (
    @NotNull(message = "Locked boolean cannot be null")
    boolean locked
) {}
