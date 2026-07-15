package com.example.tts_in_spring.post;

import com.example.tts_in_spring.notification.NotificationService;
import com.example.tts_in_spring.player.Player;
import com.example.tts_in_spring.player.PlayerTestBuilder;
import com.example.tts_in_spring.post.dto.PostRequest;
import com.example.tts_in_spring.post.dto.PostResponse;
import com.example.tts_in_spring.post.dto.PostResponseLite;
import com.example.tts_in_spring.tournament.Tournament;
import com.example.tts_in_spring.tournament.TournamentFinder;
import com.example.tts_in_spring.tournament.TournamentTestBuilder;
import com.example.tts_in_spring.tournament.dto.TournamentResponseLite;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class PostServiceTest {
    @Mock
    private PostRepository postRepository;

    @Mock
    private PostMapper postMapper;

    @Mock
    private PostFinder postFinder;

    @Mock
    private TournamentFinder tournamentFinder;

    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private PostService postService;

    private PostResponse buildPostResponse() {
        return new PostResponse(
                10000000L,
                "Test Title",
                "Test Content",
                new TournamentResponseLite(
                        10L,
                        "Test Tournament",
                        "REGISTRATION",
                        false
                )
        );
    }

    private PostRequest buildPostRequest(Tournament tournament) {
        return new PostRequest(
                "Test Title",
                "Test Content",
                tournament.getId()
        );
    }

    @Test
    void getAllPosts_returnsMappedList() {
        Post post = PostTestBuilder.aPost().build();
        PostResponse response = buildPostResponse();

        when(postRepository.findAll()).thenReturn(List.of(post));
        when(postMapper.toResponse(post)).thenReturn(response);

        List<PostResponse> result = postService.getAllPosts();

        assertThat(result).containsExactly(response);
    }

    @Test
    void getAllPosts_whenEmpty_returnsEmptyList() {
        when(postRepository.findAll()).thenReturn(List.of());

        assertThat(postService.getAllPosts()).isEmpty();
    }

    @Test
    void getPostById_whenHost_returnsMappedResponse() {
        Post post = PostTestBuilder.aPost().build();
        Tournament tournament = TournamentTestBuilder.aTournament().build();
        PostResponse response = buildPostResponse();

        when(postFinder.getPostOrThrow(post.getId())).thenReturn(post);
        when(postMapper.toResponse(post)).thenReturn(response);

        assertThat(postService.getPostById(post.getId(), tournament.getHost().getId())).isEqualTo(response);
    }

    @Test
    void getPostById_whenPlayer_returnsMappedResponse() {
        Post post = PostTestBuilder.aPost().build();
        Player player = PlayerTestBuilder.aPlayer().build();
        PostResponse response = buildPostResponse();


        when(postFinder.getPostOrThrow(post.getId())).thenReturn(post);
        when(postMapper.toResponse(post)).thenReturn(response);

        assertThat(postService.getPostById(post.getId(), player.getUser().getId())).isEqualTo(response);
    }

    @Test
    void createPost_savesAndReturnsMappedLite() {
        Tournament tournament = TournamentTestBuilder.aTournament().build();
        PostRequest request = buildPostRequest(tournament);

        Post saved = PostTestBuilder.aPost().build();
        PostResponseLite lite = new PostResponseLite(
                10000000L,
                "Test Title",
                "Test Content"
        );

        when(tournamentFinder.getTournamentOrThrow(tournament.getId())).thenReturn(tournament);
        when(postMapper.toEntity(request)).thenReturn(saved);
        when(postRepository.save(any(Post.class))).thenReturn(saved);
        when(postMapper.toResponseLite(saved)).thenReturn(lite);

        assertThat(postService.createPost(request, tournament.getHost().getId())).isEqualTo(lite);
    }

    @Test
    void deletePost_whenHost_deletesPost() {
        Post post = PostTestBuilder.aPost().build();

        when(postFinder.getPostOrThrow(post.getId())).thenReturn(post);

        postService.delete(post.getId(), post.getTournament().getHost().getId());

        verify(postRepository).delete(post);
    }
}
