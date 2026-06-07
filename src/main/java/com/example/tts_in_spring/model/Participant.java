package com.example.tts_in_spring.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "participant", schema = "public")
@Getter
@Setter
@NoArgsConstructor
public class Participant extends Base {
    @Column(name = "result_text", nullable = false)
    private String resultText;

    @Column(name = "is_winner", nullable = false)
    private boolean isWinner;

    @Column(name = "status", nullable = false)
    private String status;

    @ManyToOne
    @JoinColumn(name = "team_id")
    private Team team;

    @ManyToOne
    @JoinColumn(name = "player_id")
    private Player player;

    @ManyToOne
    @JoinColumn(name = "match_id", nullable = false)
    private Match match;
}
