package com.example.tts_in_spring.tournament.dto;

import com.example.tts_in_spring.category.dto.CategoryResponseLite;
import com.example.tts_in_spring.post.dto.PostResponseLite;
import com.example.tts_in_spring.user.dto.UserResponseLite;

import java.util.List;

public record TournamentResponse (
    Long id,
    String name,
    String stage,
    String code,
    Boolean showMobile,
    UserResponseLite host,
    List<CategoryResponseLite> categories,
    List<PostResponseLite> posts
) {}
