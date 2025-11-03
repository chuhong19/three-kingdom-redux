package com.example.three_kingdom_backend.user;

import com.example.three_kingdom_backend.util.response.StandardResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api")
public class ProfileController {

    private final ProfileService profileService;

    public ProfileController(ProfileService profileService) {
        this.profileService = profileService;
    }

    @GetMapping("/me")
    public StandardResponse<Map<String, Object>> getCurrentUser() {
        return profileService.getCurrentUserInfo();
    }
}
