package com.example.tts_in_spring.player;

import com.example.tts_in_spring.category.Category;
import com.example.tts_in_spring.base.Base;
import com.example.tts_in_spring.team.Team;
import com.example.tts_in_spring.user.User;
import com.example.tts_in_spring.participant.Participant;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "player", schema = "public")
@Getter
@Setter
@NoArgsConstructor
public class Player extends Base {
    @Column(nullable = false)
    private boolean male;

    @Column(nullable = false)
    private boolean seeded;

    @Column(nullable = false)
    private int rank;

    @Column(nullable = false)
    private String mobCode;

    @Column(nullable = false)
    private String mobile;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @ManyToOne
    @JoinColumn(name = "team_id")
    private Team team;

    @OneToMany(mappedBy = "player", fetch = FetchType.LAZY)
    private List<Participant> participants = new ArrayList<>();

    public String getMobileNumber() {
        return mobCode + " " + mobile;
    }
}