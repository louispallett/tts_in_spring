package com.example.tts_in_spring.category;

import com.example.tts_in_spring.tournament.Tournament;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class CategoryRequest {
    @NotBlank(message = "Name is required")
    private String name;

    @NotNull(message = "Tournament must not be null")
    private Tournament tournament;
}
