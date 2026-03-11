package com.angebot.backend.auth;

import com.angebot.backend.auth.jwt.JwtService;
import com.angebot.backend.entity.User;
import com.angebot.backend.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock private UserRepository repo;
    @Mock private PasswordEncoder encoder;
    @Mock private JwtService jwt;

    @InjectMocks private AuthService authService;

    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setEmail("max@test.de");
        user.setPasswordHash("hashed_password");
    }

    // ─── register ─────────────────────────────────────────────────────────────

    @Test
    @DisplayName("register - yeni kullanıcı kaydedilir")
    void shouldRegisterNewUser() {
        when(repo.existsByEmail("max@test.de")).thenReturn(false);
        when(encoder.encode("password123")).thenReturn("hashed_password");

        authService.register(new RegisterRequest("max@test.de", "password123"));

        verify(repo, times(1)).save(any(User.class));
    }

    @Test
    @DisplayName("register - email büyük harf ile gelirse küçük harfe çevrilir")
    void shouldNormalizeEmailToLowercase() {
        when(repo.existsByEmail("max@test.de")).thenReturn(false);
        when(encoder.encode(anyString())).thenReturn("hashed");

        authService.register(new RegisterRequest("MAX@TEST.DE", "password123"));

        verify(repo).existsByEmail("max@test.de");
        verify(repo).save(argThat(u -> u.getEmail().equals("max@test.de")));
    }

    @Test
    @DisplayName("register - email zaten varsa exception fırlatır")
    void shouldThrowIfEmailAlreadyExists() {
        when(repo.existsByEmail("max@test.de")).thenReturn(true);

        assertThatThrownBy(() ->
                authService.register(new RegisterRequest("max@test.de", "password123")))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("EMAIL_ALREADY_EXISTS");

        verify(repo, never()).save(any());
    }

    // ─── login ────────────────────────────────────────────────────────────────

    @Test
    @DisplayName("login - geçerli credentials ile token döner")
    void shouldReturnTokenOnValidLogin() {
        when(repo.findByEmail("max@test.de")).thenReturn(Optional.of(user));
        when(encoder.matches("password123", "hashed_password")).thenReturn(true);
        when(jwt.generateToken("max@test.de")).thenReturn("jwt-token");

        AuthResponse response = authService.login(
                new LoginRequest("max@test.de", "password123"), 3600L);

        assertThat(response.token()).isEqualTo("jwt-token");
        assertThat(response.expiresInSeconds()).isEqualTo(3600L);
    }

    @Test
    @DisplayName("login - email bulunamazsa exception fırlatır")
    void shouldThrowIfEmailNotFound() {
        when(repo.findByEmail("yok@test.de")).thenReturn(Optional.empty());

        assertThatThrownBy(() ->
                authService.login(new LoginRequest("yok@test.de", "password123"), 3600L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("INVALID_CREDENTIALS");
    }

    @Test
    @DisplayName("login - yanlış şifre ile exception fırlatır")
    void shouldThrowIfPasswordWrong() {
        when(repo.findByEmail("max@test.de")).thenReturn(Optional.of(user));
        when(encoder.matches("yanlis", "hashed_password")).thenReturn(false);

        assertThatThrownBy(() ->
                authService.login(new LoginRequest("max@test.de", "yanlis"), 3600L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("INVALID_CREDENTIALS");
    }

    @Test
    @DisplayName("login - email büyük harf ile gelirse normalize edilir")
    void shouldNormalizeEmailOnLogin() {
        when(repo.findByEmail("max@test.de")).thenReturn(Optional.of(user));
        when(encoder.matches("password123", "hashed_password")).thenReturn(true);
        when(jwt.generateToken("max@test.de")).thenReturn("jwt-token");

        AuthResponse response = authService.login(
                new LoginRequest("MAX@TEST.DE", "password123"), 3600L);

        assertThat(response.token()).isEqualTo("jwt-token");
        verify(repo).findByEmail("max@test.de");
    }
}