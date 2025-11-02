package com.example.three_kingdom_backend.auth;

import lombok.Data;

@Data
public class RefreshTokenRequest {
    private String refreshToken;
}
