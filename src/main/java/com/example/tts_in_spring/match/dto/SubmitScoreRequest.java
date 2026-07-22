package com.example.tts_in_spring.match.dto;

import com.example.tts_in_spring.participant.dto.ParticipantSubmitScoreRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

public record SubmitScoreRequest(
        @NotNull
        @Size(min = 2, max = 2, message = "Length of participants must be exactly 2")
        List<@Valid ParticipantSubmitScoreRequest> participants
) {}
