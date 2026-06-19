package com.example.tts_in_spring.player;

import com.example.tts_in_spring.team.Team;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Null;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PlayerRequest {
    @NotNull(message = "Male boolean cannot be null")
    private boolean male;

    @NotNull(message = "Seeded boolean cannot be null")
    private boolean seeded;

    @NotNull(message = "Rank int cannot be null")
    private int rank;

    @NotNull(message = "userId cannot be null")
    private Long userId;

    @NotNull(message = "categoryId cannot be null")
    private Long categoryId;

    @Null(message = "Team must be null")
    private Team team;
}
