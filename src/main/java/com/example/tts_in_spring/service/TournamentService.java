package com.example.tts_in_spring.service;

import com.example.tts_in_spring.dto.tournament.TournamentRequest;
import com.example.tts_in_spring.dto.tournament.TournamentResponse;
import com.example.tts_in_spring.dto.tournament.TournamentResponseHost;
import com.example.tts_in_spring.mapper.TournamentMapper;
import com.example.tts_in_spring.model.Tournament;
import com.example.tts_in_spring.model.User;
import com.example.tts_in_spring.repository.TournamentRepository;
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

    // FIXME: Authenticate only tournament players
    // FIXME: Show code to only host
    public TournamentResponse getTournamentById(Long id) {
        Tournament tournament = tournamentRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Tournament not found"));
        return tournamentMapper.toResponse(tournament);
    }

    public TournamentResponseHost createTournament(TournamentRequest tournamentRequest) {
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
        return tournamentMapper.toResponseHost(savedTournament);
    }
}
