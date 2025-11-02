package com.example.three_kingdom_backend.auth;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import com.example.three_kingdom_backend.user.Auditable;
import com.example.three_kingdom_backend.user.UserEntity;

import java.time.Instant;

@Entity
@Table(name = "refresh_tokens")
@Data
@EqualsAndHashCode(callSuper = false)
public class RefreshToken extends Auditable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 500)
    private String token;

    @Column(name = "expiry_date", nullable = false)
    private Instant expiryDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    @Column(nullable = false)
    private Boolean revoked = false;
}
