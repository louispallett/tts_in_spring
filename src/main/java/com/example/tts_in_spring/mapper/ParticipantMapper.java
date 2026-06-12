package com.example.tts_in_spring.mapper;

import com.example.tts_in_spring.dto.participant.ParticipantResponse;
import com.example.tts_in_spring.dto.participant.ParticipantResponseLite;
import com.example.tts_in_spring.model.Participant;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ParticipantMapper {
    ParticipantResponse toResponse(Participant participant);

    ParticipantResponseLite toResponseLite(Participant participant);
}
