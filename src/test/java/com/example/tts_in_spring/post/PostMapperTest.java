package com.example.tts_in_spring.post;

import com.example.tts_in_spring.post.dto.PostResponse;
import com.example.tts_in_spring.post.dto.PostResponseLite;
import com.example.tts_in_spring.tournament.dto.TournamentResponseLite;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import static org.assertj.core.api.Assertions.assertThat;

public class PostMapperTest {
    private final PostMapper postMapper = Mappers.getMapper(PostMapper.class);

    @Test
    void toResponse_mapsAllFields() {
        PostResponse response = postMapper.toResponse(
                PostTestBuilder.aPost().build()
        );

        assertThat(response.id()).isEqualTo(10000000L);
        assertThat(response.title()).isNotEmpty();
        assertThat(response.content()).isNotEmpty();
        assertThat(response.tournament()).isInstanceOf(TournamentResponseLite.class);
    }

    @Test
    void toResponseLite_mapsAllFields() {
        PostResponseLite response = postMapper.toResponseLite(
                PostTestBuilder.aPost().build()
        );

        assertThat(response.id()).isEqualTo(10000000L);
        assertThat(response.title()).isNotEmpty();
        assertThat(response.content()).isNotEmpty();
    }
}
