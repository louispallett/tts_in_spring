package com.example.tts_in_spring.user.dto;

import com.example.tts_in_spring.notification.dto.NotificationResponseLite;
import com.example.tts_in_spring.observer.Observer;
import com.example.tts_in_spring.player.dto.PlayerResponseLite;
import com.example.tts_in_spring.tournament.dto.TournamentResponseLite;

import java.util.List;

public record UserResponse(
        Long id,
        String firstName,
        String lastName,
        String email,
        boolean receivesEmails,
        List<TournamentResponseLite> tournaments,
        List<PlayerResponseLite> players,
        List<Observer> observers,
        List<NotificationResponseLite> notifications
) {}