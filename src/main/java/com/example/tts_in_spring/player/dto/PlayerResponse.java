package com.example.tts_in_spring.player.dto;

import com.example.tts_in_spring.category.dto.CategoryResponseLite;
import com.example.tts_in_spring.participant.dto.ParticipantResponseLite;
import com.example.tts_in_spring.team.dto.TeamResponseLite;
import com.example.tts_in_spring.user.dto.UserResponseLite;

import java.util.List;

public record PlayerResponse (
        Long id,
        String name,
        Boolean male,
        Boolean seeded,
        int rank,
        String mobCode,
        String mobile,
        UserResponseLite user,
        TeamResponseLite team,
        CategoryResponseLite category,
        List<ParticipantResponseLite> participants
) {}
