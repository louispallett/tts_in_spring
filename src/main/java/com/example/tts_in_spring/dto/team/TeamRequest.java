package com.example.tts_in_spring.dto.team;

import com.example.tts_in_spring.model.Category;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TeamRequest {
    @NotNull(message = "Category must not be null")
    private Category category;
}
