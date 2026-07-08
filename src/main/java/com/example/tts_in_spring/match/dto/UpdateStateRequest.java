package com.example.tts_in_spring.match.dto;

import com.example.tts_in_spring.match.State;
import jakarta.validation.constraints.NotNull;

public record UpdateStateRequest(@NotNull State state) {}
