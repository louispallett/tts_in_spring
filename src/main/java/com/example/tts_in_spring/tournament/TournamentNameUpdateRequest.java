package com.example.tts_in_spring.tournament;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TournamentNameUpdateRequest {
    @NotBlank(message = "Name is required")
    private String name;
}
