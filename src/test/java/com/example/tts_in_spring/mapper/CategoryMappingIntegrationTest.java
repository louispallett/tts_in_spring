package com.example.tts_in_spring.mapper;

import com.example.tts_in_spring.builder.CategoryTestBuilder;
import com.example.tts_in_spring.builder.MatchTestBuilder;
import com.example.tts_in_spring.builder.PlayerTestBuilder;
import com.example.tts_in_spring.dto.category.CategoryResponse;
import com.example.tts_in_spring.dto.match.MatchResponseLite;
import com.example.tts_in_spring.dto.player.PlayerResponseLite;
import com.example.tts_in_spring.model.Category;
import com.example.tts_in_spring.model.Match;
import com.example.tts_in_spring.model.Player;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@Import({
        CategoryMapperImpl.class,
        PlayerMapperImpl.class,
        TeamMapperImpl.class,
        MatchMapperImpl.class,
        ParticipantMapperImpl.class
})
public class CategoryMappingIntegrationTest {
    @Autowired
    private CategoryMapper categoryMapper;

    @Test
    void toResponse_mapsAllScalarFields() {
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
    void toResponse_withNoPlayers_returnsEmptyList() {
        CategoryResponse response = categoryMapper.toResponse(CategoryTestBuilder.aCategory().build());

        assertThat(response.players()).isNotNull().isEmpty();
    }

    @Test
    void toResponse_withPlayers_mapsFullChain() {
        Category category = CategoryTestBuilder.aCategory().build();
        Player player = PlayerTestBuilder.aPlayer().build();
        category.getPlayers().add(player);

        CategoryResponse response = categoryMapper.toResponse(category);

        assertThat(response.players()).hasSize(1);

        PlayerResponseLite mapped = response.players().getFirst();
        assertThat(mapped.id()).isEqualTo(1000L);
        assertThat(mapped.male()).isTrue();
        assertThat(mapped.seeded()).isFalse();
        assertThat(mapped.rank()).isEqualTo(0);
    }

    @Test
    void toResponse_withMatches_mapsFullChain() {
        Category category = CategoryTestBuilder.aCategory().build();
        Match match = MatchTestBuilder.aMatch().build();
        category.getMatches().add(match);

        CategoryResponse response = categoryMapper.toResponse(category);

        assertThat(response.matches()).hasSize(1);

        MatchResponseLite mapped = response.matches().getFirst();
        assertThat(mapped.id()).isEqualTo(100000L);
    }
}
