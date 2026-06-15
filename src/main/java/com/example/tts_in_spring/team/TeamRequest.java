package com.example.tts_in_spring.team;

import com.example.tts_in_spring.category.Category;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TeamRequest {
    @NotNull(message = "Category must not be null")
    private Category category;
}
