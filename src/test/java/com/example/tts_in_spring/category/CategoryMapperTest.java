package com.example.tts_in_spring.category;

import com.example.tts_in_spring.mapper.CategoryMapperImpl;
import com.example.tts_in_spring.player.PlayerMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@Import({CategoryMapperImpl.class})
public class CategoryMapperTest {
    @MockitoBean
    private PlayerMapper playerMapper;

    @Autowired
    private CategoryMapper categoryMapper;

    @Test
    void toResponse_mapsAllFields() {
        CategoryResponse response = categoryMapper.toResponse(CategoryTestBuilder.aCategory().build());

        assertThat(response.id()).isEqualTo(100L);
        assertThat(response.name()).isEqualTo("Mens Singles");
        assertThat(response.locked()).isFalse();
        assertThat(response.doubles()).isFalse();
        assertThat(response.tournament()).isNotNull();
        assertThat(response.tournament().id()).isEqualTo(10L);
        assertThat(response.tournament().name()).isEqualTo("Test Tournament");
    }

    @Test
    void toResponseLite_mapsAllFields() {
        CategoryResponseLite response = categoryMapper.toResponseLite(CategoryTestBuilder.aCategory().build());

        assertThat(response.id()).isEqualTo(100L);
        assertThat(response.name()).isEqualTo("Mens Singles");
        assertThat(response.doubles()).isFalse();
        assertThat(response.locked()).isFalse();
    }
}
