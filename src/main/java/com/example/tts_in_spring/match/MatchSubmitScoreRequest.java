package com.example.tts_in_spring.match;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MatchSubmitScoreRequest {
    @NotNull(message = "State must not be null")
    private String state;
}
