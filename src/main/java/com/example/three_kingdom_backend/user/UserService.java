package com.example.three_kingdom_backend.user;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.example.three_kingdom_backend.util.response.StandardResponse;
import com.example.three_kingdom_backend.user.UserRepository;
import com.example.three_kingdom_backend.user.UserEntity;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
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
        // Encode password before saving
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);
        return StandardResponse.createMessage("200", "User created successfully");
    }

    public UserEntity getUserByUsername(String username) {
        return userRepository.findByUsername(username).orElseThrow(() -> new RuntimeException("User not found"));
    }

}
