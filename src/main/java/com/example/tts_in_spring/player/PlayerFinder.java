package com.example.tts_in_spring.player;

import com.example.tts_in_spring.exception.ResourceNotFoundException;
import com.example.tts_in_spring.exception.ForbiddenException;
import com.example.tts_in_spring.tournament.Tournament;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PlayerFinder {
    private final PlayerRepository playerRepository;

    public Player getPlayerOrThrow(Long id) {
        return playerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Player " + id + " not found"));
    }

    public boolean isPlayerInCategory(Long userId, Long categoryId) {
        return playerRepository.findByUserIdAndCategoryId(userId, categoryId).isPresent();
    }

    public boolean isPlayerInTournament(Long userId, Long tournamentId) {
        return playerRepository.existsByUserIdAndCategory_TournamentId(userId, tournamentId);
    }

    public void assertHost(Player player, Long userId) {
        if (!player.getCategory().getTournament().getHost().getId().equals(userId)) {
            throw new ForbiddenException(
                    "Not host of tournament "
                            + player.getCategory().getTournament().getName()
                            + " ("
                            + player.getCategory().getTournament().getId()
                            + ")"
            );
        }
    }

    public List<Tournament> getPlayingTournaments(Long userId) {
        return playerRepository.findPlayingTournaments(userId);
    }
}
