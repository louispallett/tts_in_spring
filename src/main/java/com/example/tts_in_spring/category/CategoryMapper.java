package com.example.tts_in_spring.category;

import com.example.tts_in_spring.player.PlayerMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring", uses = {PlayerMapper.class})
public interface CategoryMapper {
    CategoryResponse toResponse(Category category);

    CategoryResponseLite toResponseLite(Category category);

    @Mapping(target = "id", ignore = true)
    Category toEntity(CategoryRequest request);

    void updateLockedEntity(CategoryLockedUpdateRequest request, @MappingTarget Category category);
}
