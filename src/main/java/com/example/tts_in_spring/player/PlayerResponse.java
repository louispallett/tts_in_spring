package com.example.tts_in_spring.player;

import com.example.tts_in_spring.category.CategoryResponseLite;
import com.example.tts_in_spring.participant.ParticipantResponseLite;
import com.example.tts_in_spring.team.TeamResponseLite;
import com.example.tts_in_spring.user.UserResponseLite;

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
