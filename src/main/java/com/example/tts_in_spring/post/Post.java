package com.example.tts_in_spring.post;

import com.example.tts_in_spring.base.Base;
import com.example.tts_in_spring.tournament.Tournament;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "post", schema = "public")
@Getter
@Setter
@NoArgsConstructor
public class Post extends Base {
    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "banner_url")
    private String bannerUrl;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;

    @ManyToOne
    @JoinColumn(name = "tournament_id", nullable = false)
    private Tournament tournament;
}
