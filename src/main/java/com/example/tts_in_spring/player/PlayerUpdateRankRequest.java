package com.example.tts_in_spring.player;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PlayerUpdateRankRequest {
    @NotNull(message = "Rank cannot be null")
    private int rank;
}
