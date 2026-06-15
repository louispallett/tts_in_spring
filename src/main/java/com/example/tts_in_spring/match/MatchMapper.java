package com.example.tts_in_spring.match;

import com.example.tts_in_spring.participant.ParticipantMapper;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = {MatchMapper.class, ParticipantMapper.class})
public interface MatchMapper {
    MatchResponse toResponse(Match match);

    MatchResponseLite toResponseLite(Match match);
}
