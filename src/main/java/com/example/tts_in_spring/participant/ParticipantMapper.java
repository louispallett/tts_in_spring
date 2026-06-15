package com.example.tts_in_spring.participant;

import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ParticipantMapper {
    ParticipantResponse toResponse(Participant participant);

    ParticipantResponseLite toResponseLite(Participant participant);
}
