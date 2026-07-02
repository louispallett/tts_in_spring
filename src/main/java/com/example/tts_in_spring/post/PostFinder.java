package com.example.tts_in_spring.post;

import com.example.tts_in_spring.exception.ForbiddenException;
import com.example.tts_in_spring.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PostFinder {
    private final PostRepository postRepository;

    public Post getPostOrThrow(Long id) {
        return postRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Post " + id + " not found"));
    }

    public void assertHost(Post post, Long userId) {
        if (!post.getTournament().getHost().getId().equals(userId)) {
            throw new ForbiddenException(
                    "Not host of tournament "
                            + post.getTournament().getName()
                            + " ("
                            + post.getTournament().getId()
                            + ")"
            );

        }
    }
}
