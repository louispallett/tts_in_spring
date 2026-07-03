package com.example.tts_in_spring.post;

import com.example.tts_in_spring.exception.ForbiddenException;
import com.example.tts_in_spring.exception.ResourceNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class PostFinderTest {
    @Mock
    private PostRepository postRepository;

    @InjectMocks
    private PostFinder postFinder;

    @Test
    void getPostOrThrow_whenPostExists_returnsPost() {
        Post post = PostTestBuilder.aPost().build();

        when(postRepository.findById(post.getId())).thenReturn(Optional.of(post));

        assertThat(postFinder.getPostOrThrow(post.getId())).isEqualTo(post);
    }

    @Test
    void getPostOrThrow_whenPostDoesNotExist_throws404() {
        when(postRepository.findById(99999999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> postFinder.getPostOrThrow(99999999L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void assertHost_whenHost_doesNotThrow() {
        Post post = PostTestBuilder.aPost().build();

        assertThatCode(() -> postFinder.assertHost(post, post.getTournament().getHost().getId()))
                .doesNotThrowAnyException();
    }

    @Test
    void assertHost_whenNotHost_throws403() {
        assertThatThrownBy(() -> postFinder.assertHost((PostTestBuilder.aPost().build()), 3L))
                .isInstanceOf(ForbiddenException.class);
    }
}
