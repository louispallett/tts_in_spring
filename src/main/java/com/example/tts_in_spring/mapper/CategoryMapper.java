package com.example.tts_in_spring.mapper;

import com.example.tts_in_spring.dto.category.CategoryResponse;
import com.example.tts_in_spring.dto.category.CategoryResponseLite;
import com.example.tts_in_spring.model.Category;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = {PlayerMapper.class})
public interface CategoryMapper {
    CategoryResponse toResponse(Category category);

    CategoryResponseLite toResponseLite(Category category);
}
