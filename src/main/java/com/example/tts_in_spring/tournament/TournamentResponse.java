package com.example.tts_in_spring.tournament;

import com.example.tts_in_spring.category.CategoryResponseLite;
import com.example.tts_in_spring.user.UserResponseLite;

import java.util.List;

public record TournamentResponse (
    Long id,
    String name,
    String stage,
    Boolean showMobile,
    UserResponseLite host,
    List<CategoryResponseLite> categories
) {}
