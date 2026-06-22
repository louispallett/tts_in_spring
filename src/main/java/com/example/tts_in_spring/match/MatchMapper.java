package com.example.tts_in_spring.match;

import com.example.tts_in_spring.match.dto.*;
import com.example.tts_in_spring.participant.ParticipantMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring", uses = {MatchMapper.class, ParticipantMapper.class})
public interface MatchMapper {
    MatchResponse toResponse(Match match);

    MatchResponseLite toResponseLite(Match match);

    @Mapping(target = "id", ignore = true)
    Match toEntity(MatchRequest request);

    void submitScoreEntity(MatchSubmitScoreRequest request, @MappingTarget Match match);
    void updateDeadlineEntity(MatchUpdateDeadlineRequest request, @MappingTarget Match match);
}
