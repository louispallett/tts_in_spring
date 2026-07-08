package com.example.tts_in_spring.participant.dto;

import com.example.tts_in_spring.participant.Status;
import jakarta.validation.constraints.NotNull;

public record ParticipantUpdateStatusRequest (@NotNull Status status) {}