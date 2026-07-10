package com.example.tts_in_spring.observer;

import com.example.tts_in_spring.base.Base;
import com.example.tts_in_spring.tournament.Tournament;
import com.example.tts_in_spring.user.User;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "observers", schema = "public")
@Getter
@Setter
@NoArgsConstructor
public class Observer extends Base {
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "tournament_id", nullable = false)
    private Tournament tournament;
}
