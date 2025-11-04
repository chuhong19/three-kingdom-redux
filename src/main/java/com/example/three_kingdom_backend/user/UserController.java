package com.example.three_kingdom_backend.user;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import com.example.three_kingdom_backend.config.security.AuthUser;
import com.example.three_kingdom_backend.user.MeDto;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @GetMapping("/me")
    public MeDto me(@AuthenticationPrincipal AuthUser me) {
        return new MeDto(me.getId(), me.getUsername());
    }

}
