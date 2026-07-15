package com.example.tts_in_spring.notification.dto;

import com.example.tts_in_spring.notification.NotificationType;
import com.example.tts_in_spring.user.dto.UserResponseLite;

import java.time.Instant;

public record NotificationResponse(
        Long id,
        String text,
        NotificationType type,
        boolean read,
        Long tournamentId,
        Long targetId,
        Instant dateCreated,
        UserResponseLite user
) {}
