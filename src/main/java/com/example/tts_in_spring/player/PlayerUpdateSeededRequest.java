package com.example.tts_in_spring.player;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PlayerUpdateSeededRequest {
    @NotNull(message = "Player Seeded boolean cannot be null")
    private boolean seeded;
}
