package com.example.tts_in_spring.team;

import com.example.tts_in_spring.participant.ParticipantMapper;
import com.example.tts_in_spring.player.PlayerMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {PlayerMapper.class, ParticipantMapper.class})
public interface TeamMapper {
    TeamResponse toResponse(Team team);

    TeamResponseLite toResponseLite(Team team);

    @Mapping(target = "id", ignore = true)
    Team toEntity(TeamRequest teamRequest);
}
