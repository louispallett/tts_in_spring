package com.example.tts_in_spring.dto.player;

import com.example.tts_in_spring.dto.category.CategoryResponseLite;
import com.example.tts_in_spring.dto.participant.ParticipantResponseLite;
import com.example.tts_in_spring.dto.team.TeamResponseLite;
import com.example.tts_in_spring.dto.user.UserResponseLite;

import java.util.List;

public record PlayerResponse (
        Long id,
        Boolean male,
        Boolean seeded,
        int rank,
        UserResponseLite user,
        TeamResponseLite team,
        CategoryResponseLite category,
        List<ParticipantResponseLite> participants
) {}
