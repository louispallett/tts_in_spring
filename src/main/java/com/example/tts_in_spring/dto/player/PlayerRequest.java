package com.example.tts_in_spring.dto.player;

import com.example.tts_in_spring.model.Category;
import com.example.tts_in_spring.model.Team;
import com.example.tts_in_spring.model.User;
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

    @NotNull(message = "User cannot be null")
    private User user;

    @NotNull(message = "Category cannot be null")
    private Category category;

    @Null
    private Team team;
}
