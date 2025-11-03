package com.example.three_kingdom_backend.auth;

import com.example.three_kingdom_backend.user.UserEntity;
import com.example.three_kingdom_backend.user.UserService;
import com.example.three_kingdom_backend.util.response.StandardResponse;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    private final UserService userService;
    private final JwtService jwtService;
    private final CustomUserDetailsService userDetailsService;
    private final AuthenticationManager authenticationManager;

    public AuthService(
            UserService userService,
            JwtService jwtService,
            CustomUserDetailsService userDetailsService,
            AuthenticationManager authenticationManager) {
        this.userService = userService;
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
        this.authenticationManager = authenticationManager;
    }

    public StandardResponse<String> register(RegisterRequest request) {
        UserEntity userEntity = new UserEntity();
        userEntity.setUsername(request.getUsername());
        userEntity.setEmail(request.getEmail());
        userEntity.setPassword(request.getPassword());
        return userService.createUser(userEntity);
    }

    public StandardResponse<AuthResponse> login(LoginRequest loginRequest) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getUsername(),
                            loginRequest.getPassword()));

            UserDetails userDetails = userDetailsService.loadUserByUsername(loginRequest.getUsername());

            String accessToken = jwtService.generateToken(userDetails);
            String refreshToken = jwtService.generateRefreshToken(userDetails);

            AuthResponse authResponse = new AuthResponse(accessToken, refreshToken, "Bearer");
            return StandardResponse.create("200", authResponse);
        } catch (org.springframework.security.core.AuthenticationException e) {
            return StandardResponse.createMessage("401", "Invalid username or password");
        } catch (Exception e) {
            return StandardResponse.createMessage("500", "Login failed: " + e.getMessage());
        }
    }

    public StandardResponse<AuthResponse> refreshToken(RefreshTokenRequest request) {
        try {
            String refreshTokenString = request.getRefreshToken();

            if (!jwtService.isRefreshToken(refreshTokenString)) {
                return StandardResponse.createMessage("401", "Invalid refresh token");
            }

            String username = jwtService.extractUsername(refreshTokenString);

            try {
                if (jwtService.isTokenExpired(refreshTokenString)) {
                    return StandardResponse.createMessage("401", "Refresh token has expired");
                }
            } catch (Exception e) {
                return StandardResponse.createMessage("401", "Invalid or expired refresh token");
            }

            UserDetails userDetails = userDetailsService.loadUserByUsername(username);

            if (!jwtService.validateToken(refreshTokenString, userDetails)) {
                return StandardResponse.createMessage("401", "Invalid refresh token");
            }

            String newAccessToken = jwtService.generateToken(userDetails);
            String newRefreshToken = jwtService.generateRefreshToken(userDetails);

            AuthResponse authResponse = new AuthResponse(newAccessToken, newRefreshToken, "Bearer");
            return StandardResponse.create("200", authResponse);
        } catch (Exception e) {
            return StandardResponse.createMessage("401", "Invalid refresh token: " + e.getMessage());
        }
    }
}
