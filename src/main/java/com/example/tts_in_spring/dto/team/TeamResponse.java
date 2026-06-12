package com.example.tts_in_spring.dto.team;

import com.example.tts_in_spring.dto.category.CategoryResponseLite;
import com.example.tts_in_spring.dto.participant.ParticipantResponseLite;
import com.example.tts_in_spring.dto.player.PlayerResponseLite;

import java.util.List;

public record TeamResponse(
    Long id,
    CategoryResponseLite category,
    List<PlayerResponseLite> players,
    List<ParticipantResponseLite> participants
) {}
