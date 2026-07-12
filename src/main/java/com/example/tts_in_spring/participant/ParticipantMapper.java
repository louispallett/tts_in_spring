package com.example.tts_in_spring.participant;

import com.example.tts_in_spring.participant.dto.*;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface ParticipantMapper {
    @Mapping(target = "name", expression = "java(getParticipantName(participant))")
    ParticipantResponse toResponse(Participant participant);

    @Mapping(target = "name", expression = "java(getParticipantName(participant))")
    ParticipantResponseLite toResponseLite(Participant participant);

    default String getParticipantName(Participant participant) {
        if (participant.getPlayer() != null) {
            return participant.getPlayer().getUser().getFullName();
        }

        if (participant.getTeam() != null) {
            var players = participant.getTeam().getPlayers();

            return players.getFirst().getUser().getFullName()
                    + " and "
                    + players.getLast().getUser().getFullName();
        }

        return null;
    }

    @Mapping(target = "id", ignore = true)
    Participant toEntity(ParticipantRequest participantRequest);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void submitScore(ParticipantSubmitScoreRequest request, @MappingTarget Participant participant);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateResultText(UpdateResultTextRequest request, @MappingTarget Participant participant);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateWinner(ParticipantUpdateWinnerRequest request, @MappingTarget Participant participant);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateStatus(ParticipantUpdateStatusRequest request, @MappingTarget Participant participant);
}
