package com.example.tts_in_spring.observer;

import com.example.tts_in_spring.exception.ForbiddenException;
import com.example.tts_in_spring.exception.ResourceNotFoundException;
import com.example.tts_in_spring.tournament.Tournament;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ObserverFinder {
    private final ObserverRepository observerRepository;

    public Observer getObserverOrThrow(Long id) {
        return observerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Observer " + id + " not found"));
    }

    public boolean isObserver(Long userId, Long tournamentId) {
        return observerRepository.findByUserIdAndTournamentId(userId, tournamentId).isPresent();
    }

    public void assertHostOrSelf(Observer observer, Long userId) {
        if (
                !observer.getTournament().getHost().getId().equals(userId)
                && !isObserver(observer.getUser().getId(), observer.getTournament().getId())
        ) {
            throw new ForbiddenException(
                    "Not host of tournament "
                            + observer.getTournament().getName()
                            + " ("
                            + observer.getTournament().getId()
                            + ") or observer themselves"
            );
        }
    }

    public List<Tournament> getObservingTournaments(Long userId) {
        return observerRepository.findObservingTournaments(userId);
    }
}
