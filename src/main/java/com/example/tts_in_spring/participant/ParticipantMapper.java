package com.example.tts_in_spring.participant;

import com.example.tts_in_spring.participant.dto.*;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface ParticipantMapper {
    @Mapping(target = "name", source = "player.user.fullName")
    ParticipantResponse toResponse(Participant participant);

    @Mapping(target = "name", source = "player.user.fullName")
    ParticipantResponseLite toResponseLite(Participant participant);

    @Mapping(target = "id", ignore = true)
    Participant toEntity(ParticipantRequest participantRequest);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void submitScore(ParticipantSubmitScoreRequest request, @MappingTarget Participant participant);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateResultText(UpdateResultTextRequest request, @MappingTarget Participant participant);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateIsWinner(ParticipantUpdateWinnerRequest request, @MappingTarget Participant participant);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateStatus(ParticipantUpdateStatusRequest request, @MappingTarget Participant participant);
}
