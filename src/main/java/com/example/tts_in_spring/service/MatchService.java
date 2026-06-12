package com.example.tts_in_spring.service;

import com.example.tts_in_spring.dto.match.MatchRequest;
import com.example.tts_in_spring.dto.match.MatchResponse;
import com.example.tts_in_spring.dto.match.MatchResponseLite;
import com.example.tts_in_spring.mapper.MatchMapper;
import com.example.tts_in_spring.model.Match;
import com.example.tts_in_spring.repository.MatchRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class MatchService {
    @Autowired
    private MatchRepository matchRepository;

    @Autowired
    private MatchMapper matchMapper;

    public List<MatchResponse> getAllMatches() {
        return matchRepository.findAll()
                .stream()
                .map(matchMapper::toResponse)
                .toList();
    }

    public MatchResponse getMatchById(Long id) {
        Match match = matchRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Match not found"));
        return matchMapper.toResponse(match);
    }

    public MatchResponseLite createMatch(MatchRequest matchRequest) {
        Match validatedMatch = new Match();

        validatedMatch.setTournamentRoundText(matchRequest.getTournamentRoundText());
        validatedMatch.setState("SCHEDULED");
        validatedMatch.setDate(matchRequest.getDate());
        validatedMatch.setUpdateNumber(0);
        validatedMatch.setQualifyingMatch(matchRequest.isQualifyingMatch());
        validatedMatch.setCategory(matchRequest.getCategory());

        Match savedMatch = matchRepository.save(validatedMatch);
        return matchMapper.toResponseLite(savedMatch);
    }
}
