package com.example.tts_in_spring.category;

import com.example.tts_in_spring.player.PlayerMapper;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = {PlayerMapper.class})
public interface CategoryMapper {
    CategoryResponse toResponse(Category category);

    CategoryResponseLite toResponseLite(Category category);
}
