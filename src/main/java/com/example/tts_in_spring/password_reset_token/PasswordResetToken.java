package com.example.tts_in_spring.password_reset_token;

import com.example.tts_in_spring.base.Base;
import com.example.tts_in_spring.user.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Entity
@Table(name = "password_reset_token", schema = "public")
@Getter
@Setter
@NoArgsConstructor
public class PasswordResetToken extends Base {
    @Column(nullable = false, unique = true)
    private String token;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private Instant expiresAt;

    @Column(nullable = false)
    private boolean used = false;
}
