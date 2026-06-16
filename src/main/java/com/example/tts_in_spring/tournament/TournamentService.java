package com.example.tts_in_spring.tournament;

import com.example.tts_in_spring.user.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class TournamentService {
    @Autowired
    private TournamentRepository tournamentRepository;

    @Autowired
    private TournamentMapper tournamentMapper;

    public List<TournamentResponse> getAllTournaments() {
        return tournamentRepository.findAll()
                .stream()
                .map(tournamentMapper::toResponse)
                .toList();
    }

    // FIXME: Authenticate only tournament players and host
    public TournamentResponse getTournamentById(Long id) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) auth.getPrincipal();

        Tournament tournament = tournamentRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Tournament not found"));
        return tournamentMapper.toResponse(tournament);
    }

    public TournamentResponseLite createTournament(TournamentRequest tournamentRequest) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) auth.getPrincipal();

        tournamentRequest.setCode(tournamentRequest.getName());

        Tournament validatedTournament = new Tournament();
        validatedTournament.setName(tournamentRequest.getName());
        validatedTournament.setStage("SIGN_UP");
        validatedTournament.setHost(user);
        validatedTournament.setCode(tournamentRequest.getCode());
        validatedTournament.setShowMobile(tournamentRequest.isShowMobile());

        Tournament savedTournament = tournamentRepository.save(validatedTournament);
        return tournamentMapper.toResponseLite(savedTournament);
    }
}
