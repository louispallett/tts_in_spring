package com.example.tts_in_spring.participant;

import com.example.tts_in_spring.match.Match;
import com.example.tts_in_spring.base.Base;
import com.example.tts_in_spring.player.Player;
import com.example.tts_in_spring.team.Team;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "participant", schema = "public")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Participant extends Base {
    @Column(name = "result_text", nullable = false)
    private String resultText;

    @Column(name = "winner", nullable = false)
    private boolean winner;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status = Status.TBD;

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
