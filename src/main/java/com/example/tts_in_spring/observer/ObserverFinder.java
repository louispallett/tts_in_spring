package com.example.tts_in_spring.observer;

import com.example.tts_in_spring.exception.ForbiddenException;
import com.example.tts_in_spring.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ObserverFinder {
    private final ObserverRepository observerRepository;

    public Observer getObserverOrThrow(Long id) {
        return observerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Observer " + id + " not found"));
    }

    public void assertHost(Observer observer, Long userId) {
        if (!observer.getTournament().getHost().getId().equals(userId)) {
            throw new ForbiddenException(
                    "Not host of tournament "
                    + observer.getTournament().getName()
                    + " ("
                    + observer.getTournament().getId()
                    + ")"
            );
        }
    }
}
