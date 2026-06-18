package com.example.tts_in_spring.tournament;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TournamentStageUpdateRequest {
    @NotBlank(message = "Stage is required")
    private String stage;
}
