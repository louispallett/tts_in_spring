package com.example.tts_in_spring.post;


import com.example.tts_in_spring.post.dto.*;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface PostMapper {
    PostResponse toResponse(Post post);

    PostResponseLite toResponseLite(Post post);

    @Mapping(target = "id", ignore = true)
    Post toEntity(PostRequest postRequest);

    void updateTitleEntity(PostUpdateTitleRequest request, @MappingTarget Post post);

    void updateContentEntity(PostUpdateContentRequest request, @MappingTarget Post post);
}
