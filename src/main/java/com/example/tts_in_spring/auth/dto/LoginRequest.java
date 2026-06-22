package com.example.tts_in_spring.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record LoginRequest(
    @NotBlank(message = "Email must not be blank")
    @Email(message = "Must be valid email")
    String email,
    @NotBlank(message = "Password must not be blank")
    String password
) {}
