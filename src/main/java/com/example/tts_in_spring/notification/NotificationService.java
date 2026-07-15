package com.example.tts_in_spring.notification;

import com.example.tts_in_spring.emailer.EmailerService;
import com.example.tts_in_spring.match.Match;
import com.example.tts_in_spring.notification.dto.NotificationRequest;
import com.example.tts_in_spring.notification.dto.NotificationResponse;
import com.example.tts_in_spring.participant.Participant;
import com.example.tts_in_spring.player.Player;
import com.example.tts_in_spring.post.Post;
import com.example.tts_in_spring.tournament.Tournament;
import com.example.tts_in_spring.user.User;
import com.example.tts_in_spring.user.UserFinder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NotificationService {
    private final NotificationRepository notificationRepository;
    private final NotificationMapper notificationMapper;
    private final NotificationFinder notificationFinder;
    private final EmailerService emailerService;
    private final UserFinder userFinder;

    @Transactional
    public List<NotificationResponse> getAllNotifications() {
        return notificationRepository.findAll()
                .stream()
                .map(notificationMapper::toResponse)
                .toList();
    }

    @Transactional
    private void create(NotificationRequest request, User user) {
        Notification notification = notificationMapper.toEntity(request);
        notification.setUser(user);

        notificationRepository.save(notification);

        if (user.isReceivesEmails()) {
            String html = """
                    <p>Dear <b>%s</b>,</p>
                    <p>You have a new notification.</p>
                    <p>%s</p>
                    <p>Please note that you can alter your email preferences by logging into the application and navigating to <b>Settings</b> and then <b>Account</b>.</p>
                    <p>Please do not respond to this email.</p>
                    <p><i>Tennis Tournament Creator</i> by <b>Louis Pallett</b> is licensed under the GNU Affero General Public License</p>
                    """
                    .formatted(
                            user.getFirstName(),
                            request.text()
                    );

            emailerService.sendEmail(
                    user.getEmail(),
                    "TTS: New Notification",
                    html
            );
        }
    }

    @Transactional
    public void handleWelcomeNotification(User user) {
        create(
                new NotificationRequest(
                        "Welcome to Tennis Tournament Creator! This message confirms you have successfully registered your account.",
                        NotificationType.WELCOME,
                        null,
                        null,
                        null
                ),
                user
        );
    }

    @Transactional
    public void handleJoinTournamentNotification(List<Player> players) {
        String categories = players.stream()
                .map(player -> player.getCategory().getName().getDisplayName())
                .collect(Collectors.joining(", "));

        Tournament tournament = players.getFirst().getCategory().getTournament();

        create(
                new NotificationRequest(
                        "You have joined the tournament " + tournament.getName()
                        + " in the following categories: " + categories + ".",
                        NotificationType.JOIN_TOURNAMENT,
                        tournament.getId(),
                        null,
                        null
                ),
                players.getFirst().getUser()
        );
    }

    @Transactional
    public void handleResetPasswordNotification(User user) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")
                .withZone(ZoneId.systemDefault());

        create(
                new NotificationRequest(
                        "Your password was changed in a reset request at " + formatter.format(Instant.now()),
                        NotificationType.PASSWORD_RESET,
                        null,
                        null,
                        null
                ),
                user
        );
    }

    private List<User> extractUsersFromTournament(Tournament tournament) {
        return tournament.getCategories().stream()
                .flatMap(category -> category.getPlayers().stream())
                .map(Player::getUser)
                .collect(Collectors.toMap(
                        User::getId,
                        Function.identity(),
                        (existing, replacement) -> existing
                ))
                .values()
                .stream()
                .toList();
    }

    @Transactional
    public void handleNotificationForStage(Tournament tournament) {
        NotificationRequest request = new NotificationRequest(
                tournament.getHost().getFullName() + " updated the stage of "
                        + tournament.getName() + " to " + " " + tournament.getStage() + ".",
                NotificationType.TOURNAMENT_STAGE,
                tournament.getId(),
                null,
                null
        );

        for (User user : extractUsersFromTournament(tournament)) {
            create(request, user);
        }
    }

    @Transactional
    public void handlePostCreatedNotification(Post post) {
        NotificationRequest request = new NotificationRequest(
                post.getTournament().getHost().getFullName() + " has posted a new update for "
                + post.getTournament().getName() + ".",
                NotificationType.POST_CREATED,
                post.getTournament().getId(),
                null,
                post.getId()
        );

        for (User user : extractUsersFromTournament(post.getTournament())) {
            create(request, user);
        }
    }

    @Transactional
    public void handleScoreSubmissionNotification(Match match, Long userId) {
        User user = userFinder.getUserOrThrow(userId);
        NotificationRequest request = new NotificationRequest(
                user.getFullName() + " has submitted a score for your "
                        + match.getCategory().getName().getDisplayName() + " match in the tournament "
                        + match.getCategory().getTournament().getName() + ".",
                NotificationType.RESULT_SUBMITTED,
                match.getCategory().getTournament().getId(),
                match.getCategory().getId(),
                match.getId()
        );

        for (Participant participant : match.getParticipants()) {
            if (match.getCategory().isDoubles()) {
                for (Player player : participant.getTeam().getPlayers()) {
                    create(request, player.getUser());
                }
            } else {
                create(request, participant.getPlayer().getUser());
            }
        }
    }

    @Transactional
    public void delete(Long id, Long userId) {
        Notification notification = notificationFinder.getNotificationOrThrow(id);
        notificationFinder.assertUser(notification, userId);

        notificationRepository.delete(notification);
    }
}
