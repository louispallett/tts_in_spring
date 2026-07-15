package com.example.tts_in_spring.notification;

import com.example.tts_in_spring.base.Base;
import com.example.tts_in_spring.user.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "notification", schema = "public")
@Getter
@Setter
@NoArgsConstructor
public class Notification extends Base {
    @Column(name = "text", nullable = false)
    private String text;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private NotificationType type;

    @Column(name = "read", nullable = false)
    private boolean read = false;

    @Column(name = "tournamentId")
    private Long tournamentId;

    @Column(name = "targetId")
    private Long targetId;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
}
