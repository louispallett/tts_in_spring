package com.example.tts_in_spring.tournament;

import com.example.tts_in_spring.category.CategoryMapper;
import com.example.tts_in_spring.tournament.dto.*;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring", uses = {CategoryMapper.class})
public interface TournamentMapper {
    TournamentResponse toResponse(Tournament tournament);

    TournamentResponseLite toResponseLite(Tournament tournament);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "stage", ignore = true)
    Tournament toEntity(TournamentRequest tournamentRequest);

    void updateNameEntity(TournamentNameUpdateRequest request, @MappingTarget Tournament tournament);

    void updateShowMobileEntity(TournamentShowMobileUpdateRequest request, @MappingTarget Tournament tournament);
}