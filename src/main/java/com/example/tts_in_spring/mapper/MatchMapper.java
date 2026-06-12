package com.example.tts_in_spring.mapper;

import com.example.tts_in_spring.dto.match.MatchResponse;
import com.example.tts_in_spring.dto.match.MatchResponseLite;
import com.example.tts_in_spring.model.Match;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = {MatchMapper.class, ParticipantMapper.class})
public interface MatchMapper {
    MatchResponse toResponse(Match match);

    MatchResponseLite toResponseLite(Match match);
}
