package com.angebot.backend.auth.jwt;

import com.angebot.backend.entity.User;
import com.angebot.backend.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JwtUtilTest {

    @Mock
    private UserRepository userRepo;

    @InjectMocks
    private JwtUtil jwtUtil;

    @BeforeEach
    void setUp() {
        SecurityContextHolder.clearContext();
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void shouldExtractUserIdBySubject() {
        String email = "test@example.com";
        UUID userId = UUID.randomUUID();

        User user = new User();
        user.setId(userId);
        user.setEmail(email);

        when(userRepo.findByEmail(email)).thenReturn(Optional.of(user));

        UUID result = jwtUtil.extractUserId(email);

        assertEquals(userId, result);
        verify(userRepo, times(1)).findByEmail(email);
    }

    @Test
    void shouldThrowExceptionWhenUserNotFoundInExtractUserId() {
        String email = "notfound@example.com";

        when(userRepo.findByEmail(email)).thenReturn(Optional.empty());

        assertThrows(java.util.NoSuchElementException.class, () -> jwtUtil.extractUserId(email));
        verify(userRepo, times(1)).findByEmail(email);
    }

    @Test
    void shouldGetCurrentUserIdFromSecurityContext() {
        String email = "current@example.com";
        UUID userId = UUID.randomUUID();

        User user = new User();
        user.setId(userId);
        user.setEmail(email);

        var authentication = new UsernamePasswordAuthenticationToken(email, null);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        when(userRepo.findByEmail(email)).thenReturn(Optional.of(user));

        UUID result = jwtUtil.getCurrentUserId();

        assertEquals(userId, result);
        verify(userRepo, times(1)).findByEmail(email);
    }

    @Test
    void shouldThrowExceptionWhenCurrentUserNotFound() {
        String email = "missing@example.com";

        var authentication = new UsernamePasswordAuthenticationToken(email, null);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        when(userRepo.findByEmail(email)).thenReturn(Optional.empty());

        assertThrows(java.util.NoSuchElementException.class, () -> jwtUtil.getCurrentUserId());
        verify(userRepo, times(1)).findByEmail(email);
    }
}