package com.example.tts_in_spring.auth.dto;

import jakarta.validation.constraints.NotBlank;

public record ResetPasswordRequest(
        @NotBlank(message = "Token cannot be blank")
        String token,
        @NotBlank(message = "Password is required")
        String newPassword
) {
}
