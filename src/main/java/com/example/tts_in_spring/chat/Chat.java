package com.example.tts_in_spring.chat;

import com.example.tts_in_spring.base.Base;
import com.example.tts_in_spring.match.Match;
import com.example.tts_in_spring.message.Message;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "chat", schema = "public")
@Getter
@Setter
@NoArgsConstructor
public class Chat extends Base {
    @OneToOne
    @JoinColumn(name = "match_id", nullable = false, unique = true)
    Match match;

    @OneToMany(mappedBy = "chat", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Message> messages = new ArrayList<>();
}
