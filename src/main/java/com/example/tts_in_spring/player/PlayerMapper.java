package com.example.tts_in_spring.player;

import com.example.tts_in_spring.participant.ParticipantMapper;
import com.example.tts_in_spring.player.dto.*;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring", uses = {ParticipantMapper.class})
public interface PlayerMapper {
    PlayerResponse toResponse(Player player);

    PlayerResponseLite toResponseLite(Player player);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "rank", ignore = true)
    @Mapping(target = "seeded", ignore = true)
    Player toEntity(PlayerRequest playerRequest);

    void updateRankEntity(PlayerUpdateRankRequest request, @MappingTarget Player player);

    void updateSeededEntity(PlayerUpdateSeededRequest request, @MappingTarget Player player);
}
