package com.example.tts_in_spring.player;

import com.example.tts_in_spring.participant.ParticipantMapper;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = {ParticipantMapper.class})
public interface PlayerMapper {
    PlayerResponse toResponse(Player player);

    PlayerResponseLite toResponseLite(Player player);
}
