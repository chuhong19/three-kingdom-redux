package com.example.three_kingdom_backend.auth;

import lombok.Data;

@Data
public class LoginRequest {
    private String username;
    private String password;
}
