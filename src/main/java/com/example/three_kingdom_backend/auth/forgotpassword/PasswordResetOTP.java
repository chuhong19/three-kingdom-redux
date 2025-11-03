package com.example.three_kingdom_backend.auth.forgotpassword;

import com.example.three_kingdom_backend.user.User;
import jakarta.persistence.*;
import lombok.Data;
import java.time.Instant;

@Entity
@Table(name = "password_reset_otp")
@Data
public class PasswordResetOTP {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "code_hash")
    private String codeHash;

    @Column(name = "expires_at")
    private Instant expiresAt;

    @Column(name = "attempts")
    private int attempts;

    @Column(name = "max_attempts")
    private int maxAttempts = 5;

    @Column(name = "used")
    private boolean used = false;

    @Column(name = "created_at")
    private Instant createdAt = Instant.now();
}
