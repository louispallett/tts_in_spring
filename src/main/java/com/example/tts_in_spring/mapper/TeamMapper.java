package com.example.tts_in_spring.mapper;

import com.example.tts_in_spring.dto.team.TeamResponse;
import com.example.tts_in_spring.dto.team.TeamResponseLite;
import com.example.tts_in_spring.model.Team;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = {PlayerMapper.class, ParticipantMapper.class})
public interface TeamMapper {
    TeamResponse toResponse(Team team);

    TeamResponseLite toResponseLite(Team team);
}
