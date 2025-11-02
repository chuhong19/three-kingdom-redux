package com.example.three_kingdom_backend.auth;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.example.three_kingdom_backend.util.response.StandardResponse;
import com.example.three_kingdom_backend.user.UserEntity;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public StandardResponse<String> register(@RequestBody UserEntity request) {
        return authService.register(request);
    }

    @PostMapping("/login")
    public StandardResponse<AuthResponse> login(@RequestBody LoginRequest request) {
        return authService.login(request);
    }

    @PostMapping("/refresh")
    public StandardResponse<AuthResponse> refreshToken(@RequestBody RefreshTokenRequest request) {
        return authService.refreshToken(request);
    }

    @PostMapping("/logout")
    public StandardResponse<String> logout(@RequestBody RefreshTokenRequest request) {
        return authService.logout(request.getRefreshToken());
    }

}
