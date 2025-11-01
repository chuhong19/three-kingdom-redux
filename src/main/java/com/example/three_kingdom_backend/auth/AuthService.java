package com.example.three_kingdom_backend.auth;

import org.springframework.stereotype.Service;

import com.example.three_kingdom_backend.user.UserService;
import com.example.three_kingdom_backend.user.UserEntity;

import com.example.three_kingdom_backend.util.response.StandardResponse;

@Service
public class AuthService {
    private final UserService userService;

    public AuthService(UserService userService) {
        this.userService = userService;
    }

    public StandardResponse<String> register(UserEntity data) {
        return userService.createUser(data);
    }
}
