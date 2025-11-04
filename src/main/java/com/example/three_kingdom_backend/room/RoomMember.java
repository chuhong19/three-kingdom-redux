package com.example.three_kingdom_backend.room;

import com.example.three_kingdom_backend.user.User;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ForeignKey;
import lombok.Data;
import lombok.EqualsAndHashCode;
import jakarta.persistence.Index;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(name = "room_members", uniqueConstraints = @UniqueConstraint(name = "uk_room_members_room_user", columnNames = {
        "room_id", "user_id" }), indexes = {
                @Index(name = "idx_room_members_room_id", columnList = "room_id"),
                @Index(name = "idx_room_members_user_id", columnList = "user_id")
        })
@Data
@EqualsAndHashCode(callSuper = false)
public class RoomMember {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "room_id", nullable = false, foreignKey = @ForeignKey(name = "fk_room_members_room"))
    private Room room;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false, foreignKey = @ForeignKey(name = "fk_room_members_user"))
    private User user;

}
