package com.angebot.backend.auth.jwt;


import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;

@Service
@Slf4j
public class JwtService {
    private final byte[] secretBytes;
    private final long expMinutes;

    public JwtService(@Value("${app.jwt.secret}") String secret,
                      @Value("${app.jwt.exp-minutes}") long expMinutes) {
        this.secretBytes = secret.getBytes(StandardCharsets.UTF_8);
        this.expMinutes = expMinutes;
    }

    // generate token
    public String generateToken(String subjectEmail){
        Instant now=Instant.now();
        log.info(now.toString());
        Instant exp=now.plusSeconds(expMinutes*60);
        log.info(exp.toString());
        return Jwts.builder()
                .subject(subjectEmail)
                .issuedAt(Date.from(now))
                .expiration(Date.from(exp))
                .signWith(Keys.hmacShaKeyFor(secretBytes))
                .compact();
    }

    public String extractEmail(String token){
        return Jwts.parser()
                .verifyWith(Keys.hmacShaKeyFor(secretBytes))
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }
}
