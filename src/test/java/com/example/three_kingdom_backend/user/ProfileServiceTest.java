package com.example.three_kingdom_backend.user;

import com.example.three_kingdom_backend.util.response.StandardResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("ProfileService Tests")
class ProfileServiceTest {

    @InjectMocks
    private ProfileService profileService;

    private UserDetails userDetails;
    private Authentication authentication;

    @BeforeEach
    void setUp() {
        SecurityContextHolder.clearContext();

        List<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority("ROLE_USER"));

        userDetails = org.springframework.security.core.userdetails.User.builder()
                .username("testuser")
                .password("password")
                .authorities(authorities)
                .build();

        authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(userDetails);

        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
    }

    @Test
    @DisplayName("Get current user info successfully")
    void testGetCurrentUserInfo_Success() {
        StandardResponse<Map<String, Object>> response = profileService.getCurrentUserInfo();

        assertThat(response).isNotNull();
        assertThat(response.getCode()).isEqualTo("200");
        assertThat(response.getData()).isNotNull();

        Map<String, Object> userInfo = response.getData();
        assertThat(userInfo.get("username")).isEqualTo("testuser");
        assertThat(userInfo.get("authenticated")).isEqualTo(true);
        assertThat(userInfo.get("authorities")).isNotNull();
    }

    @Test
    @DisplayName("Get current user info contains correct username")
    void testGetCurrentUserInfo_ContainsUsername() {
        StandardResponse<Map<String, Object>> response = profileService.getCurrentUserInfo();

        Map<String, Object> userInfo = response.getData();
        assertThat(userInfo.get("username")).isEqualTo("testuser");
    }

    @Test
    @DisplayName("Get current user info contains authorities")
    void testGetCurrentUserInfo_ContainsAuthorities() {
        StandardResponse<Map<String, Object>> response = profileService.getCurrentUserInfo();

        Map<String, Object> userInfo = response.getData();
        Object authorities = userInfo.get("authorities");

        assertThat(authorities).isNotNull();
        assertThat(authorities).isInstanceOf(Collection.class);
    }

    @Test
    @DisplayName("Get current user info marks as authenticated")
    void testGetCurrentUserInfo_Authenticated() {
        StandardResponse<Map<String, Object>> response = profileService.getCurrentUserInfo();

        Map<String, Object> userInfo = response.getData();
        assertThat(userInfo.get("authenticated")).isEqualTo(true);
    }

    @Test
    @DisplayName("Get current user info returns correct response structure")
    void testGetCurrentUserInfo_ResponseStructure() {
        StandardResponse<Map<String, Object>> response = profileService.getCurrentUserInfo();

        assertThat(response.getCode()).isEqualTo("200");
        assertThat(response.getData()).isNotNull();
        assertThat(response.getData()).isInstanceOf(Map.class);

        Map<String, Object> userInfo = response.getData();
        assertThat(userInfo.containsKey("username")).isTrue();
        assertThat(userInfo.containsKey("authorities")).isTrue();
        assertThat(userInfo.containsKey("authenticated")).isTrue();
    }
}
