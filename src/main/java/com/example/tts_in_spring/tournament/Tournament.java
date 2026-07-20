package com.example.tts_in_spring.tournament;

import com.example.tts_in_spring.category.Category;
import com.example.tts_in_spring.base.Base;
import com.example.tts_in_spring.post.Post;
import com.example.tts_in_spring.user.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "tournament", schema = "public")
@Getter
@Setter
@NoArgsConstructor
public class Tournament extends Base {
    @Column(nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Stage stage = Stage.REGISTRATION;

    @ManyToOne
    @JoinColumn(name = "host_id", nullable = false)
    private User host;

    @Column(nullable = false, unique = true)
    private String code;

    @Column(nullable = false)
    private boolean showMobile;

    @OneToMany(mappedBy = "tournament", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Category> categories = new ArrayList<>();

    @OneToMany(mappedBy = "tournament", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Post> posts = new ArrayList<>();
}