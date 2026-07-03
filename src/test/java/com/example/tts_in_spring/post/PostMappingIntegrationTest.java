package com.example.tts_in_spring.post;

import com.example.tts_in_spring.post.dto.PostResponse;
import com.example.tts_in_spring.tournament.dto.TournamentResponseLite;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@Import({PostMapperImpl.class})
public class PostMappingIntegrationTest {
    @Autowired
    private PostMapper postMapper;

    @Test
    void toResponse_mapsAllScalarFields() {
        PostResponse response = postMapper.toResponse(PostTestBuilder.aPost().build());

        assertThat(response.id()).isEqualTo(10000000L);
        assertThat(response.title()).isNotEmpty();
        assertThat(response.content()).isNotEmpty();
        assertThat(response.tournament()).isInstanceOf(TournamentResponseLite.class);
    }
}
