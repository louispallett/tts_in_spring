package com.example.tts_in_spring.notification.dto;

import com.example.tts_in_spring.notification.NotificationType;

import java.time.Instant;

public record NotificationResponseLite(
        Long id,
        String text,
        NotificationType type,
        boolean read,
        Long tournamentId,
        Long targetId,
        Instant dateCreated
) {}
