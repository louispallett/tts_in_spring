package com.example.tts_in_spring.observer;

import com.example.tts_in_spring.exception.ForbiddenException;
import com.example.tts_in_spring.observer.dto.ObserverRequest;
import com.example.tts_in_spring.observer.dto.ObserverResponse;
import com.example.tts_in_spring.observer.dto.ObserverResponseLite;
import com.example.tts_in_spring.tournament.Tournament;
import com.example.tts_in_spring.tournament.TournamentFinder;
import com.example.tts_in_spring.user.User;
import com.example.tts_in_spring.user.UserFinder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ObserverService {
    private final ObserverRepository observerRepository;
    private final ObserverMapper observerMapper;
    private final ObserverFinder observerFinder;
    private final UserFinder userFinder;
    private final TournamentFinder tournamentFinder;

    @Transactional(readOnly = true)
    public List<ObserverResponse> getAllObservers() {
        return observerRepository.findAll()
                .stream()
                .map(observerMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public ObserverResponse getObserverById(Long id, Long userId) {
        Observer observer = observerFinder.getObserverOrThrow(id);

        if (
                observer.getId().equals(userId) ||
                        observer.getTournament().getHost().getId().equals(userId)
        ) {
            return observerMapper.toResponse(observer);
        }

        throw new ForbiddenException();
    }

    @Transactional
    public ObserverResponseLite createObserver(ObserverRequest request, Long userId) {
        User user = userFinder.getUserOrThrow(userId);
        Tournament tournament = tournamentFinder.getTournamentOrThrow(request.tournamentId());

        Observer newObserver = observerMapper.toEntity(request);
        newObserver.setTournament(tournament);
        newObserver.setUser(user);

        Observer savedObserver = observerRepository.save(newObserver);
        return observerMapper.toResponseLite(newObserver);
    }

    public void delete(Long id, Long userId) {
        Observer observer = observerFinder.getObserverOrThrow(id);
        observerFinder.assertHost(observer, userId);

        observerRepository.delete(observer);
    }
}
