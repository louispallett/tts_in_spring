package com.example.tts_in_spring.score;

import com.example.tts_in_spring.score.dto.ScoreResponse;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ScoreMapper {
    ScoreResponse toResponse(Score score);
}
