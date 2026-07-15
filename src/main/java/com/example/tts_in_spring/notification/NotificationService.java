package com.example.tts_in_spring.notification;

import com.example.tts_in_spring.emailer.EmailerService;
import com.example.tts_in_spring.notification.dto.NotificationRequest;
import com.example.tts_in_spring.notification.dto.NotificationResponse;
import com.example.tts_in_spring.notification.dto.NotificationResponseLite;
import com.example.tts_in_spring.user.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationService {
    private final NotificationRepository notificationRepository;
    private final NotificationMapper notificationMapper;
    private final NotificationFinder notificationFinder;
    private final EmailerService emailerService;

    @Transactional
    public List<NotificationResponseLite> getAllNotifications() {
        return notificationRepository.findAll()
                .stream()
                .map(notificationMapper::toResponseLite)
                .toList();
    }

    @Transactional
    public NotificationResponse create(NotificationRequest request, User user) {
        Notification notification = notificationMapper.toEntity(request);
        notification.setUser(user);

        Notification savedNotification = notificationRepository.save(notification);

        if (user.isReceivesEmails()) {
            String html = """
                    <p>Dear <b>%s</b>,</p>
                    <p>You have a new notification.</p>
                    <p>%s</p>
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

        return notificationMapper.toResponse(savedNotification);
    }

    @Transactional
    public void delete(Long id, Long userId) {
        Notification notification = notificationFinder.getNotificationOrThrow(id);
        notificationFinder.assertUser(notification, userId);

        notificationRepository.delete(notification);
    }
}
