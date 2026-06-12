package com.example.tts_in_spring.mapper;

import com.example.tts_in_spring.dto.player.PlayerResponse;
import com.example.tts_in_spring.dto.player.PlayerResponseLite;
import com.example.tts_in_spring.model.Player;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = {ParticipantMapper.class})
public interface PlayerMapper {
    PlayerResponse toResponse(Player player);

    PlayerResponseLite toResponseLite(Player player);
}
