package com.example.tts_in_spring.message;

import com.example.tts_in_spring.base.Base;
import com.example.tts_in_spring.chat.Chat;
import com.example.tts_in_spring.participant.Participant;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "message", schema = "public")
@Getter
@Setter
@NoArgsConstructor
public class Message extends Base {
    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;

    @ManyToOne
    @JoinColumn(name = "participant_id", nullable = false)
    private Participant participant;

    @ManyToOne
    @JoinColumn(name = "chat_id", nullable = false)
    private Chat chat;
}
