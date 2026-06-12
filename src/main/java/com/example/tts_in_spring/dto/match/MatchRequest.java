package com.example.tts_in_spring.dto.match;

import com.example.tts_in_spring.model.Category;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
public class MatchRequest {
    @NotNull(message = "Tournament Round Text cannot be null")
    @NotBlank(message = "Tournament Round Text cannot be blank")
    private String tournamentRoundText;

    @NotNull(message = "State cannot be null")
    @NotBlank(message = "State cannot be blank")
    private String state;

    @NotNull(message = "Deadline cannot be null")
    private Instant date;

    @NotNull(message = "Update number cannot be null")
    private int updateNumber;

    @NotNull(message = "Qualifying match boolean cannot be null")
    private boolean qualifyingMatch;

    @NotNull(message = "Category cannot be null")
    private Category category;
}
