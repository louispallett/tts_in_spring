package com.example.tts_in_spring.user.dto;

import jakarta.validation.constraints.NotBlank;

public record DeleteRequest(
        @NotBlank(message = "Password must not be blank")
        String password
) {}
