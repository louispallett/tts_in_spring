package com.example.tts_in_spring.tournament;

import com.example.tts_in_spring.category.CategoryMapper;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = {CategoryMapper.class})
public interface TournamentMapper {
    TournamentResponse toResponse(Tournament tournament);

    TournamentResponseLite toResponseLite(Tournament tournament);

    TournamentResponseHost toResponseHost(Tournament tournament);
}