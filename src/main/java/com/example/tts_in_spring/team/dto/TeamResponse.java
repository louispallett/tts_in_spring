package com.example.tts_in_spring.team.dto;

import com.example.tts_in_spring.category.dto.CategoryResponseLite;
import com.example.tts_in_spring.participant.dto.ParticipantResponseLite;
import com.example.tts_in_spring.player.dto.PlayerResponseLite;

import java.util.List;

public record TeamResponse(
    Long id,
    CategoryResponseLite category,
    List<PlayerResponseLite> players,
    List<ParticipantResponseLite> participants
) {}
