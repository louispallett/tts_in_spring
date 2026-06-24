package com.example.tts_in_spring.match;

import com.example.tts_in_spring.category.Category;
import com.example.tts_in_spring.category.CategoryService;
import com.example.tts_in_spring.match.dto.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MatchService {
    private final MatchRepository matchRepository;
    private final MatchMapper matchMapper;
    private final CategoryService categoryService;

    public Match getMatchOrThrow(Long id) {
        return matchRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Match not found"));
    }

    public boolean isHost(Match match, Long userId) {
        return match.getCategory()
                .getTournament()
                .getHost()
                .getId()
                .equals(userId);
    }

    public boolean isParticipant(Match match, Long userId) {
        return match.getParticipants().stream()
                .anyMatch(p -> p.getPlayer().getUser().getId().equals(userId));
    }

    @Transactional(readOnly = true)
    public List<MatchResponse> getAllMatches() {
        return matchRepository.findAll()
                .stream()
                .map(matchMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public MatchResponse getMatchById(Long id, Long userId) {
        Match match = getMatchOrThrow(id);

        if (isHost(match, userId) || isParticipant(match, userId)) {
            return matchMapper.toResponse(match);
        }

        throw new ResponseStatusException(HttpStatus.FORBIDDEN);
    }

    public int calculateNumberOfRounds(int numOfPlayers) {
        return (int) Math.ceil(Math.log(numOfPlayers) / Math.log(2));
    }

    public int getNextPowerOfTwo(int n) {
        int power = 1;
        while (power < n) {
            power *= 2;
        }
        return power;
    }

    public int calculateByes(int n) {
        int nextPowerOfTwo = getNextPowerOfTwo(n);
        return nextPowerOfTwo - n;
    }

    public int roundLoopLimit(int qualPlayers, int players, int totalRounds) {
        return qualPlayers == players ? totalRounds : totalRounds - 1;
    }

    @Transactional
    public MatchResponse createMatch(MatchRequest request, Long userId) {
        Category category = categoryService.getCategoryOrThrow(request.categoryId());
        Match nextMatch = request.nextMatchId() == null ? null : getMatchOrThrow(request.nextMatchId());

        if (category.getTournament().getHost().getId().equals(userId)) {
            Match match = matchMapper.toEntity(request);
            match.setState("SCHEDULED");
            match.setCategory(category);
            match.setNextMatch(nextMatch);

            Match savedMatch = matchRepository.save(match);
            return matchMapper.toResponse(savedMatch);
        }

        throw new ResponseStatusException(HttpStatus.FORBIDDEN);
    }

    @Transactional
    public Match createGeneratedMatch(MatchRequest request) {
        Category category = categoryService.getCategoryOrThrow(request.categoryId());
        Match nextMatch = request.nextMatchId() == null ? null : getMatchOrThrow(request.nextMatchId());

        Match match = matchMapper.toEntity(request);
        match.setState("SCHEDULED");
        match.setCategory(category);
        match.setNextMatch(nextMatch);

        return matchRepository.save(match);
    }

    @Transactional
    public List<List<Match>> generateAndSaveMatches(Category category, int numOfParticipants) {
        int totalRounds = calculateNumberOfRounds(numOfParticipants);
        System.out.println(totalRounds);
        List<List<Match>> matchesByRound = new ArrayList<>();
        int numOfQualParticipants = numOfParticipants - calculateByes(numOfParticipants);

        Match finalMatch = createGeneratedMatch(
                new MatchRequest(
                        String.valueOf(totalRounds),
                        Instant.now(),
                        false,
                        category.getId(),
                        null
                )
        );
        matchesByRound.add(List.of(finalMatch));

        int round = 1;

        while(round < roundLoopLimit(numOfQualParticipants, numOfParticipants, totalRounds)) {
            List<Match> currentRoundMatches = new ArrayList<>();
            for (int i = 0; i < matchesByRound.getLast().size() * 2; i++) {
                Match nextMatch = matchesByRound.get(round - 1).get(i / 2);
                Match newMatch = createGeneratedMatch(
                        new MatchRequest(
                                String.valueOf(totalRounds - round),
                                Instant.now(),
                                false,
                                category.getId(),
                                nextMatch.getId()
                        )
                );
                currentRoundMatches.add(newMatch);
            }
            matchesByRound.add(currentRoundMatches);
            round++;
        }

        return matchesByRound;
    }

    public List<List<Match>> splitIntoFours(List<Match> matches) {
        List<Match> arr = new ArrayList<>(matches);
        List<List<Match>> result = new ArrayList<>();
        for (int i = 0; i < matches.size(); i += 4) {
            int end = Math.min(i + 4, arr.size());
            result.add(new ArrayList<>(arr.subList(i, end)));
        }

        return result;
    }

    public List<List<Match>> reorderGroups(List<List<Match>> matches) {
        List<List<Match>> result = new ArrayList<>();
        for (int i = 0; i < matches.size(); i++) {
            if (i % 2 == 0) {
                result.add(matches.get(i));
            } else {
                result.add(matches.get(matches.size() - i));
            }
        }

        return result;
    }

    public List<Match> reorderArray(List<List<Match>> matches) {
        if (matches.getFirst().size() < 3) return matches.stream().flatMap(List::stream).toList();

        int[] order = {0, 3, 2, 1};
        List<Match> result = new ArrayList<>();

        for (int index : order) {
            for (List<Match> subList : matches) {
                result.add(subList.get(index));
            }
        }

        return result;
    }

    // Notes on this one...
    // It may actually make more sense to create our participants first, and then pass them in here. That will then allow
    // us to simply pass in the participants. The number of players/teams will equal the number of participants here, so
    // if we pass in the full entity, we'll still get access to information such as rank (although, the participants must
    // be passed in order of rank to the function)
    // public List<MatchResponse> generateMatchesForCategory(List<Participant> participants) {
        // Step 1: Generate matches
            // Note: The function in the TS file is called 'generateFirstRoundMatches', but this is misleading, it actually
            // returns a MatchTypeLite[][] of all matches grouped by rounds

        // Step 2: Get the first round

        // Step 3: Split the first round into groups of four (splitIntoFours)

        // Step 4: Reorder groups, passing in result of step 3

        // Step 5: Reorder array, passing in result of step 4

        // Step 6: Participant creation loop - this is where we create the participants for each match.
            // int n = matchesOrdered.size()
            // for (int i = 0; i < 2 * n; i++) {
                // int index = (i < n) ? i : (2 * n - i - 1);
                // Participant is first participant in participant list. // Note: we may be able to use i as an index here, as originally
                // we used participant.shift(), which removes and returns first element in array. However, in Java we can't do this, but what
                // we can do is create a Queue and user .poll(), which basically does the same thing as shift:
                // Queue<Participant> queue = new ArrayDeque<>(qualifyingParticipants);
                // if (queue.isEmpty()) {
                //     Participant participant = queue.poll(); // Same as shift()
                //     // handle adding matchesOrdered[index] to participant.setMatch()
                // } else {
                // break;
                // }

        // Step 7: Handle creating qualifying matches

        // Step 8: final two loops to add qualifying round participants to qualifying matches
    // }

    @Transactional
    public MatchResponseLite submitScore(Long id, MatchSubmitScoreRequest request, Long userId) {
        Match match = getMatchOrThrow(id);

        if (isHost(match, userId) || isParticipant(match, userId)) {
            matchMapper.submitScoreEntity(request, match);

            Match savedMatch = matchRepository.save(match);
            return matchMapper.toResponseLite(savedMatch);
        }

        throw new ResponseStatusException(HttpStatus.FORBIDDEN);
    }

    @Transactional
    public MatchResponseLite updateDeadline(Long id, MatchUpdateDeadlineRequest request, Long userId) {
        Match match = getMatchOrThrow(id);

        if (isHost(match, userId)) {
            matchMapper.updateDeadlineEntity(request, match);

            Match savedMatch = matchRepository.save(match);
            return matchMapper.toResponseLite(savedMatch);
        }

        throw new ResponseStatusException(HttpStatus.FORBIDDEN);
    }

    @Transactional
    public void delete(Long id, Long userId) {
        Match match = getMatchOrThrow(id);

        if (!isHost(match, userId)) throw new ResponseStatusException(HttpStatus.FORBIDDEN);

        matchRepository.delete(match);
    }
}
