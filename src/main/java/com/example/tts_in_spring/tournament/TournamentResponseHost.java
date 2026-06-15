package com.example.tts_in_spring.tournament;

import com.example.tts_in_spring.category.CategoryResponseLite;
import com.example.tts_in_spring.user.UserResponseLite;

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
