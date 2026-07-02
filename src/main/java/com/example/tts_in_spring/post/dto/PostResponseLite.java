package com.example.tts_in_spring.post.dto;

public record PostResponseLite(
        Long id,
        String title,
        String content
) {}
