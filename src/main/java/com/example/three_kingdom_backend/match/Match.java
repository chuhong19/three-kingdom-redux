package com.example.three_kingdom_backend.match;

import com.example.three_kingdom_backend.match.enums.MatchStatus;
import com.example.three_kingdom_backend.match.enums.EnumKingdom;
import com.example.three_kingdom_backend.room.Room;
import com.example.three_kingdom_backend.user.User;
import com.example.three_kingdom_backend.util.Auditable;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
import jakarta.persistence.EnumType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.FetchType;
import jakarta.persistence.Table;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.JoinTable;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;
import java.util.ArrayList;

@Entity
@Table(name = "matches")
@Data
@EqualsAndHashCode(callSuper = false)
public class Match extends Auditable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "room_id", nullable = false, foreignKey = @ForeignKey(name = "fk_match_room"))
    private Room room;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "wei_player_id", nullable = false, foreignKey = @ForeignKey(name = "fk_match_wei_player"))
    private User weiPlayer;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "shu_player_id", nullable = false, foreignKey = @ForeignKey(name = "fk_match_shu_player"))
    private User shuPlayer;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "wu_player_id", nullable = false, foreignKey = @ForeignKey(name = "fk_match_wu_player"))
    private User wuPlayer;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private MatchStatus status;

    @Enumerated(EnumType.STRING)
    @Column(name = "current_turn", length = 10)
    private EnumKingdom currentTurn;

    @Column(name = "is_wei_turn", nullable = false)
    private boolean isWeiTurn;

    @Column(name = "is_shu_turn", nullable = false)
    private boolean isShuTurn;

    @Column(name = "is_wu_turn", nullable = false)
    private boolean isWuTurn;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "match_active_players", joinColumns = @JoinColumn(name = "match_id", foreignKey = @ForeignKey(name = "fk_match_active_players_match")), inverseJoinColumns = @JoinColumn(name = "user_id", foreignKey = @ForeignKey(name = "fk_match_active_players_user")))
    private List<User> activePlayers = new ArrayList<>();
}
