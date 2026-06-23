package com.langcenter.assetmanagement.service;

import com.langcenter.assetmanagement.dto.auth.LoginRequest;
import com.langcenter.assetmanagement.dto.auth.LoginResponse;
import com.langcenter.assetmanagement.entity.Role;
import com.langcenter.assetmanagement.entity.User;
import com.langcenter.assetmanagement.repository.UserRepository;
import com.langcenter.assetmanagement.security.JwtTokenProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtTokenProvider tokenProvider;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService; // Login is in UserService in our code

    private User mockUser;
    private Role mockRole;

    @BeforeEach
    void setUp() {
        mockRole = Role.builder().roleName("ADMIN").build();
        mockUser = User.builder()
                .username("admin")
                .password("encodedPassword")
                .fullName("Administrator")
                .role(mockRole)
                .isActive(true)
                .build();
    }

    @Test
    void testLoginSuccess() {
        LoginRequest request = new LoginRequest();
        request.setUsername("admin");
        request.setPassword("admin123");

        Authentication authentication = mock(Authentication.class);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(tokenProvider.generateToken(authentication)).thenReturn("mockJwtToken");
        when(userRepository.findByUsername("admin")).thenReturn(Optional.of(mockUser));

        LoginResponse response = userService.login(request);

        assertNotNull(response);
        assertEquals("mockJwtToken", response.getToken());
        assertEquals("admin", response.getUsername());
        assertEquals("ADMIN", response.getRole());
        verify(authenticationManager, times(1)).authenticate(any(UsernamePasswordAuthenticationToken.class));
    }
}
