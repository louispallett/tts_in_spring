package com.example.tts_in_spring.match;

import com.example.tts_in_spring.category.Category;
import com.example.tts_in_spring.base.Base;
import com.example.tts_in_spring.participant.Participant;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "match", schema = "public")
@Getter
@Setter
@NoArgsConstructor
public class Match extends Base {
    @Column(name = "tournament_round_text", nullable = false)
    private String tournamentRoundText;

    @Column(name = "state", nullable = false)
    private String state;

    @Column(name = "date", nullable = false)
    private Instant date;

    @Column(name = "update_number", nullable = false)
    private int updateNumber;

    @Column(name = "qualifying_match", nullable = false)
    private boolean qualifyingMatch;

    @ManyToOne
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @ManyToOne
    @JoinColumn(name = "next_match_id")
    private Match nextMatch;

    @OneToMany(mappedBy = "nextMatch", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Match> previousMatches = new ArrayList<>();

    @OneToMany(mappedBy = "match", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Participant> participants = new ArrayList<>();
}
