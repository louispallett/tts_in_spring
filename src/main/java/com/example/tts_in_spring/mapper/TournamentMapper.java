package com.example.tts_in_spring.mapper;

import com.example.tts_in_spring.dto.tournament.TournamentResponse;
import com.example.tts_in_spring.dto.tournament.TournamentResponseHost;
import com.example.tts_in_spring.dto.tournament.TournamentResponseLite;
import com.example.tts_in_spring.model.Tournament;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = {CategoryMapper.class})
public interface TournamentMapper {
    TournamentResponse toResponse(Tournament tournament);

    TournamentResponseLite toResponseLite(Tournament tournament);

    TournamentResponseHost toResponseHost(Tournament tournament);
}