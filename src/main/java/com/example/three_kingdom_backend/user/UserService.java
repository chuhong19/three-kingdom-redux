package com.example.three_kingdom_backend.user;

import org.springframework.stereotype.Service;
import com.example.three_kingdom_backend.util.response.StandardResponse;
import com.example.three_kingdom_backend.user.UserRepository;
import com.example.three_kingdom_backend.user.UserEntity;

@Service
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public StandardResponse<String> createUser(UserEntity user) {
        String username = user.getUsername();
        if (userRepository.findByUsername(username).isPresent()) {
            return StandardResponse.createMessage("400", "Username already exists");
        }
        String email = user.getEmail();
        if (userRepository.findByEmail(email).isPresent()) {
            return StandardResponse.createMessage("400", "Email already exists");
        }
        userRepository.save(user);
        return StandardResponse.createMessage("200", "User created successfully");
    }

    public UserEntity getUserByUsername(String username) {
        return userRepository.findByUsername(username).orElseThrow(() -> new RuntimeException("User not found"));
    }

}
