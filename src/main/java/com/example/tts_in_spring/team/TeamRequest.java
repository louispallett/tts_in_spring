package com.example.tts_in_spring.team;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TeamRequest {
    @NotNull(message = "categoryId must not be null")
    private Long categoryId;
}
