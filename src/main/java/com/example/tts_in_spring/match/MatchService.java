package com.example.tts_in_spring.match;

import com.example.tts_in_spring.category.Category;
import com.example.tts_in_spring.category.CategoryFinder;
import com.example.tts_in_spring.exception.SeedingAlgorithmException;
import com.example.tts_in_spring.exception.ForbiddenException;
import com.example.tts_in_spring.match.dto.*;
import com.example.tts_in_spring.participant.Participant;
import com.example.tts_in_spring.participant.ParticipantService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class MatchService {
    private final MatchRepository matchRepository;
    private final MatchMapper matchMapper;
    private final CategoryFinder categoryFinder;
    private final MatchFinder matchFinder;
    private final ParticipantService participantService;

    @Transactional(readOnly = true)
    public List<MatchResponse> getAllMatches() {
        return matchRepository.findAll()
                .stream()
                .map(matchMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public MatchResponse getMatchById(Long id, Long userId) {
        Match match = matchFinder.getMatchOrThrow(id);

        if (matchFinder.isHost(match, userId) || matchFinder.isParticipant(match, userId)) {
            return matchMapper.toResponse(match);
        }

        throw new ForbiddenException("Not host of tournament or participant in match");
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
        Category category = categoryFinder.getCategoryOrThrow(request.categoryId());
        Match nextMatch = request.nextMatchId() == null ? null : matchFinder.getMatchOrThrow(request.nextMatchId());

        if (category.getTournament().getHost().getId().equals(userId)) {
            Match match = matchMapper.toEntity(request);
            match.setState("SCHEDULED");
            match.setCategory(category);
            match.setNextMatch(nextMatch);

            Match savedMatch = matchRepository.save(match);
            return matchMapper.toResponse(savedMatch);
        }

        throw new ForbiddenException("Not host of tournament " + category.getTournament().getName());
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

    private List<Participant> getByeInParticipants(List<Participant> participants) {
        int byeInParticipantsNum = participants.size() - calculateByes(participants.size());
        if (byeInParticipantsNum == participants.size()) {
            return participants;
        } else {
            int end = participants.size() - byeInParticipantsNum;
            List<Participant> result = new ArrayList<>(participants.subList(0, end));
            participants.subList(0, end).clear();
            return result;
        }
    }

    public List<List<Match>> generateMatches(Category category, int numOfParticipants) {
        int totalRounds = calculateNumberOfRounds(numOfParticipants);
        List<List<Match>> matchesByRound = new ArrayList<>();
        int numOfQualParticipants = numOfParticipants - calculateByes(numOfParticipants);

        Match finalMatch = new Match();
        finalMatch.setTournamentRoundText(String.valueOf(totalRounds));
        finalMatch.setState("SCHEDULED");
        finalMatch.setDeadline(Instant.now());
        finalMatch.setQualifyingMatch(false);
        finalMatch.setCategory(category);
        matchesByRound.add(List.of(finalMatch));

        int round = 1;

        while(round < roundLoopLimit(numOfQualParticipants, numOfParticipants, totalRounds)) {
            List<Match> currentRoundMatches = new ArrayList<>();
            for (int i = 0; i < matchesByRound.getLast().size() * 2; i++) {
                Match nextMatch = matchesByRound.get(round - 1).get(i / 2);

                Match newMatch = new Match();
                newMatch.setTournamentRoundText(String.valueOf(totalRounds - round));
                newMatch.setState("SCHEDULED");
                newMatch.setDeadline(Instant.now());
                newMatch.setQualifyingMatch(false);
                newMatch.setCategory(category);
                newMatch.setNextMatch(nextMatch);

                currentRoundMatches.add(newMatch);
            }
            matchesByRound.add(currentRoundMatches);
            round++;
        }

        if (matchesByRound.isEmpty()) {
            throw new SeedingAlgorithmException("Match creation failed. Returned Empty List");
        }

        return matchesByRound;
    }

    private List<Match> orderMatches(List<Match> firstRound) {
        List<List<Match>> intoFours = splitIntoFours(firstRound);
        List<List<Match>> groupsOrdered = reorderGroups(intoFours);

        return reorderArray(groupsOrdered);
    }

    private List<Participant> addQualifyingPlayersToMatches(
            List<Participant> qualifyingParticipants,
            List<Match> firstRoundOrdered
    ) {
        List<Participant> finalParticipants = new ArrayList<>();
        int n = firstRoundOrdered.size();
        for (int i = 0; i < 2 * n; i++) {
            int index = (i < n) ? i : (2 * n - i - 1);
            Queue<Participant> queue = new ArrayDeque<>(qualifyingParticipants);
            if (!queue.isEmpty()) {
                Participant participant = queue.poll();
                participant.setMatch(firstRoundOrdered.get(index));
                finalParticipants.add(participant);
                qualifyingParticipants.subList(0, 1).clear();
            } else {
                break;
            }
        }
        return finalParticipants;
    }

    private List<Match> generateQualifyingMatches(
            List<Participant> participants,
            Category category,
            List<Match> matchesOrdered
    ) {
        int numOfQualifyingMatches = participants.size() / 2; // Because we removed qualifying players, participants just contains the players which must play
        List<Match> qualifyingMatches = new ArrayList<>();

        int n = matchesOrdered.size();

        for (int i = 0; i < 2 * n; i++) {
            int index = (i < n) ? i : (2 * n - i - 1);
            if (numOfQualifyingMatches > 0) {

                Match match = new Match();
                match.setTournamentRoundText("1");
                match.setState("SCHEDULED");
                match.setDeadline(Instant.now());
                match.setQualifyingMatch(true);
                match.setCategory(category);
                match.setNextMatch(matchesOrdered.get(index));

                qualifyingMatches.add(match);
                numOfQualifyingMatches--;
            } else {
                break;
            }
        }

        return  qualifyingMatches;
    }

    private List<Participant> addQualifyingParticipantsToMatches(
            List<Participant> qualifyingParticipants,
            List<Participant> participants,
            List<Match> qualifyingMatches
    ) {
        List<Participant> finalParticipants = new ArrayList<>(qualifyingParticipants);

        for (Match match : qualifyingMatches) {
            Participant participant = participants.removeLast();
            participant.setMatch(match);
            finalParticipants.add(participant);
        }

        for (Match match : qualifyingMatches.reversed()) {
            Participant participant = participants.removeFirst();
            participant.setMatch(match);
            finalParticipants.add(participant);
        }

        return finalParticipants;
    }

    @Transactional
    List<MatchResponse> generateMatchesParent(Long categoryId, Long userId) {
        Category category = categoryFinder.getCategoryOrThrow(categoryId);
        categoryFinder.assertHost(category, userId);

        // Generate participants (not saved)
        List<Participant> participants = participantService.generateParticipants(category);

        List<List<Match>> matches = generateMatches(category, participants.size());
        List<Match> firstRound = matches.removeLast();
        List<Match> firstRoundOrdered = orderMatches(firstRound);

        // FIXME: Ensure that participants list is actually affected here. We need qualified players to actually be removed here.
        List<Participant> byeInParticipants = getByeInParticipants(participants);

        List<Participant> qualifyingParticipantsFinal = addQualifyingPlayersToMatches(byeInParticipants, firstRoundOrdered);

        List<Match> qualifyingMatches = generateQualifyingMatches(participants, category, firstRoundOrdered);

        List<Participant> participantsFinal = addQualifyingParticipantsToMatches(
                qualifyingParticipantsFinal,
                participants,
                qualifyingMatches
        );

        List<Match> matchesFlattened = matches.stream().flatMap(List::stream).toList();
        List<Match> finalMatches = Stream.of(qualifyingMatches, firstRoundOrdered, matchesFlattened).flatMap(List::stream).toList();

        List<Match> savedMatches = matchRepository.saveAll(finalMatches);
        participantService.saveAllParticipants(participantsFinal);

        return savedMatches.stream().map(matchMapper::toResponse).toList();
    }

    @Transactional
    public MatchResponseLite submitScore(Long id, Long userId) {
        Match match = matchFinder.getMatchOrThrow(id);

        if (matchFinder.isHost(match, userId) || matchFinder.isParticipant(match, userId)) {
            MatchSubmitScoreRequest request = new MatchSubmitScoreRequest("SCORE_DONE");
            matchMapper.submitScoreEntity(request, match);

            Match savedMatch = matchRepository.save(match);
            return matchMapper.toResponseLite(savedMatch);
        }

        throw new ForbiddenException(
                "Not host of "
                        + match.getCategory().getTournament().getName()
                        + " ("
                        + match.getCategory().getTournament().getId()
                        + ") or participant in match " + match.getId()
        );
    }

    @Transactional
    public MatchResponseLite updateDeadline(Long id, MatchUpdateDeadlineRequest request, Long userId) {
        Match match = matchFinder.getMatchOrThrow(id);
        matchFinder.assertHost(match, userId);

        matchMapper.updateDeadlineEntity(request, match);

        Match savedMatch = matchRepository.save(match);
        return matchMapper.toResponseLite(savedMatch);
    }

    @Transactional
    public void delete(Long id, Long userId) {
        Match match = matchFinder.getMatchOrThrow(id);
        matchFinder.assertHost(match, userId);

        matchRepository.delete(match);
    }
}
