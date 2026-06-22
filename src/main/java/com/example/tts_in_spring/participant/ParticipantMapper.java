package com.example.tts_in_spring.participant;

import com.example.tts_in_spring.participant.dto.*;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface ParticipantMapper {
    ParticipantResponse toResponse(Participant participant);

    ParticipantResponseLite toResponseLite(Participant participant);

    @Mapping(target = "id", ignore = true)
    Participant toEntity(ParticipantRequest participantRequest);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void submitScore(ParticipantSubmitScoreRequest request, @MappingTarget Participant participant);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateResultText(ParticipantUpdateResultTextRequest request, @MappingTarget Participant participant);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateIsWinner(ParticipantUpdateWinnerRequest request, @MappingTarget Participant participant);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateStatus(ParticipantUpdateStatusRequest request, @MappingTarget Participant participant);
}
