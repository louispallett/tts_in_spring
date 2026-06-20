package com.example.tts_in_spring.match;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
public class MatchUpdateDeadlineRequest {
    @NotNull(message = "Deadline must not be null")
    public Instant deadline;
}
