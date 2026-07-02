package com.example.tts_in_spring.post.dto;

import com.example.tts_in_spring.tournament.dto.TournamentResponseLite;

public record PostResponse(
        Long id,
        String title,
        String content,
        TournamentResponseLite tournament
) {}
