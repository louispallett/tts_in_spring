package com.example.tts_in_spring.team;

import com.example.tts_in_spring.category.CategoryResponseLite;
import com.example.tts_in_spring.participant.ParticipantResponseLite;
import com.example.tts_in_spring.player.PlayerResponseLite;

import java.util.List;

public record TeamResponse(
    Long id,
    CategoryResponseLite category,
    List<PlayerResponseLite> players,
    List<ParticipantResponseLite> participants
) {}
