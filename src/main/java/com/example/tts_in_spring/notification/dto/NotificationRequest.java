package com.example.tts_in_spring.notification.dto;

import com.example.tts_in_spring.notification.NotificationType;
import com.example.tts_in_spring.user.User;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record NotificationRequest(
        @NotBlank String text,
        @NotNull NotificationType type,
        Long tournamentId,
        Long targetId,
        User user
) {}
