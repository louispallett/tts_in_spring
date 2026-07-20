package com.example.tts_in_spring.score;

import com.example.tts_in_spring.base.Base;
import com.example.tts_in_spring.match.Match;
import com.example.tts_in_spring.user.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Immutable;

@Entity
@Immutable
@Table(name = "score", schema = "public")
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Score extends Base {
    @OneToOne
    @JoinColumn(name = "match_id", nullable = false, unique = true)
    Match match;

    @ManyToOne
    @JoinColumn(name = "submitted_by", nullable = false)
    User submittedBy;
}
