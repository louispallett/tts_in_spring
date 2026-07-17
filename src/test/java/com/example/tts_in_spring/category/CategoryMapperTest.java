package com.example.tts_in_spring.category;

import com.example.tts_in_spring.category.dto.CategoryResponse;
import com.example.tts_in_spring.category.dto.CategoryResponseLite;
import com.example.tts_in_spring.player.PlayerMapper;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import static org.assertj.core.api.Assertions.assertThat;

public class CategoryMapperTest {
    @MockitoBean
    private PlayerMapper playerMapper;

    private final CategoryMapper categoryMapper = Mappers.getMapper(CategoryMapper.class);

    @Test
    void toResponse_mapsAllFields() {
        CategoryResponse response = categoryMapper.toResponse(CategoryTestBuilder.aCategory().build());

        assertThat(response.id()).isEqualTo(100L);
        assertThat(response.name()).isEqualTo("Men's Singles");
        assertThat(response.doubles()).isFalse();
        assertThat(response.tournament()).isNotNull();
        assertThat(response.tournament().id()).isEqualTo(10L);
        assertThat(response.tournament().name()).isEqualTo("Test Tournament");
    }

    @Test
    void toResponseLite_mapsAllFields() {
        CategoryResponseLite response = categoryMapper.toResponseLite(CategoryTestBuilder.aCategory().build());

        assertThat(response.id()).isEqualTo(100L);
        assertThat(response.name()).isEqualTo("Men's Singles");
        assertThat(response.doubles()).isFalse();
    }
}
