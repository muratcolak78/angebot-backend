package com.angebot.backend.auth.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

class JwtServiceTest {

    private JwtService jwtService;

    private final String secret = "my-super-secret-key-that-is-at-least-32-bytes!";
    private final long expMinutes = 15;

    @BeforeEach
    void setUp() {
        jwtService = new JwtService(secret, expMinutes);
    }

    @Test
    void shouldGenerateTokenSuccessfully() {
        String token = jwtService.generateToken("test@example.com");

        assertNotNull(token);
        assertFalse(token.isBlank());
    }

    @Test
    void shouldExtractEmailFromValidToken() {
        String email = "test@example.com";
        String token = jwtService.generateToken(email);

        String extractedEmail = jwtService.extractEmail(token);

        assertEquals(email, extractedEmail);
    }

    @Test
    void shouldThrowExceptionWhenTokenIsInvalid() {
        String invalidToken = "this.is.not.a.valid.token";

        assertThrows(Exception.class, () -> jwtService.extractEmail(invalidToken));
    }

    @Test
    void shouldCreateTokenWithCorrectSubject() {
        String email = "user@mail.com";
        String token = jwtService.generateToken(email);

        Claims claims = Jwts.parser()
                .verifyWith(Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8)))
                .build()
                .parseSignedClaims(token)
                .getPayload();

        assertEquals(email, claims.getSubject());
        assertNotNull(claims.getIssuedAt());
        assertNotNull(claims.getExpiration());
    }

    @Test
    void shouldSetExpirationApproximatelyCorrect() {
        String email = "test@example.com";

        long beforeGeneration = System.currentTimeMillis();
        String token = jwtService.generateToken(email);
        long afterGeneration = System.currentTimeMillis();

        Claims claims = Jwts.parser()
                .verifyWith(Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8)))
                .build()
                .parseSignedClaims(token)
                .getPayload();

        Date issuedAt = claims.getIssuedAt();
        Date expiration = claims.getExpiration();

        assertNotNull(issuedAt);
        assertNotNull(expiration);

        long expectedDurationMillis = expMinutes * 60 * 1000;
        long actualDurationMillis = expiration.getTime() - issuedAt.getTime();

        assertEquals(expectedDurationMillis, actualDurationMillis);

       assertTrue(issuedAt.getTime() <= afterGeneration);
    }
}