package com.example.three_kingdom_backend.room;

import com.example.three_kingdom_backend.util.Auditable;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Column;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.Enumerated;
import jakarta.persistence.EnumType;
import lombok.Data;
import lombok.EqualsAndHashCode;

import com.example.three_kingdom_backend.user.User;

import java.util.Date;

enum STATUS {
    OPENING,
    PLAYING,
    DONE
}

@Entity
@Table(name = "rooms")
@Data
@EqualsAndHashCode(callSuper = false)
public class Room extends Auditable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "owner_id", nullable = false, foreignKey = @ForeignKey(name = "fk_room_owner"))
    private User owner;

    private String password;

    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private STATUS status;

    // Public getters for Auditable fields
    public Date getCreatedAt() {
        return super.createdAt;
    }

    public Date getUpdatedAt() {
        return super.updatedAt;
    }

}
