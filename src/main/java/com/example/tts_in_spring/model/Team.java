package com.example.tts_in_spring.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/* Note on Team model
 * Note that this model really takes all it's static information dynamically from its players array. For example, I have
 * removed 'rank' since we can simply get this by calculating:
 *      player[0].rank + player[1].rank
 * Equally, we can display the name by concatenating:
 *      player[0].firstName + " " + player[0].lastname + " and " + player[1].firstName + " " + player[1].lastname
 * Note that the players array must ALWAYS be equal to exactly 2 in size. Add a checker when returning this information.
*/

@Entity
@Table(name = "team", schema = "public")
@Getter
@Setter
@NoArgsConstructor
public class Team extends Base {
    @ManyToOne
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @OneToMany(mappedBy = "team", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Player> players = new ArrayList<>();

    @OneToMany(mappedBy = "team", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Participant> participants = new ArrayList<>();
}
