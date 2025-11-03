package com.example.three_kingdom_backend.user;

import com.example.three_kingdom_backend.util.response.StandardResponse;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class ProfileService {

    public StandardResponse<Map<String, Object>> getCurrentUserInfo() {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext()
                .getAuthentication()
                .getPrincipal();

        Map<String, Object> userInfo = new HashMap<>();
        userInfo.put("username", userDetails.getUsername());
        userInfo.put("authorities", userDetails.getAuthorities());
        userInfo.put("authenticated", true);

        return StandardResponse.create("200", userInfo);
    }
}
