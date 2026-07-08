package com.example.tts_in_spring.match.dto;

import com.example.tts_in_spring.participant.dto.ParticipantSubmitScoreRequest;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record SubmitScoreRequest(
        @NotNull
        List<ParticipantSubmitScoreRequest> participants
) {}
