package com.example.tts_in_spring.post;

import com.example.tts_in_spring.exception.ForbiddenException;
import com.example.tts_in_spring.post.dto.*;
import com.example.tts_in_spring.tournament.Tournament;
import com.example.tts_in_spring.tournament.TournamentFinder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PostService {
    private final PostRepository postRepository;
    private final PostMapper postMapper;
    private final PostFinder postFinder;
    private final TournamentFinder tournamentFinder;

    @Transactional(readOnly = true)
    public List<PostResponse> getAllPosts() {
        return postRepository.findAll()
                .stream()
                .map(postMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public PostResponse getPostById(Long id, Long userId) {
        Post post = postFinder.getPostOrThrow(id);
        postFinder.assertHost(post, userId);

        return postMapper.toResponse(post);
    }

    @Transactional
    public PostResponseLite createPost(PostRequest request, Long userId) {
        Tournament tournament = tournamentFinder.getTournamentOrThrow(request.tournamentId());

        if (!tournament.getHost().getId().equals(userId)) throw new ForbiddenException();

        Post post = postMapper.toEntity(request);
        post.setTournament(tournament);

        Post savedPost = postRepository.save(post);
        return postMapper.toResponseLite(savedPost);
    }

    @Transactional
    public PostResponseLite updateTitle(Long id, PostUpdateTitleRequest request, Long userId) {
        Post post = postFinder.getPostOrThrow(id);
        postFinder.assertHost(post, userId);

        postMapper.updateTitleEntity(request, post);
        return postMapper.toResponseLite(post);
    }

    @Transactional
    public PostResponseLite updateContent(Long id, PostUpdateContentRequest request, Long userId) {
        Post post = postFinder.getPostOrThrow(id);
        postFinder.assertHost(post, userId);

        postMapper.updateContentEntity(request, post);
        return postMapper.toResponseLite(post);
    }

    @Transactional
    public void delete(Long id, Long userId) {
        Post post = postFinder.getPostOrThrow(id);
        postFinder.assertHost(post, userId);

        postRepository.delete(post);
    }
}
