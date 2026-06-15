package com.example.tts_in_spring.participant;

import com.example.tts_in_spring.match.Match;
import com.example.tts_in_spring.player.Player;
import com.example.tts_in_spring.team.Team;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ParticipantRequest {
    @NotNull(message = "Result Text cannot be null")
    private String resultText;

    @NotNull(message = "isWinner boolean cannot be null")
    private boolean isWinner;

    @NotNull(message = "Status cannot be null")
    @NotBlank(message = "Status cannot be blank")
    private String status;

    private Team team;

    private Player player;

    @NotNull(message = "Team cannot be null")
    private Match match;
}
