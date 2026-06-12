package com.example.tts_in_spring.dto.tournament;

import com.example.tts_in_spring.dto.category.CategoryResponseLite;
import com.example.tts_in_spring.dto.user.UserResponseLite;

import java.util.List;

public record TournamentResponseHost(
        Long id,
        String name,
        String stage,
        String code,
        Boolean showMobile,
        UserResponseLite host,
        List<CategoryResponseLite> categories
) {}
