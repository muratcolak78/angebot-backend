package com.angebot.backend.auth;

public record AuthResponse(
        String token,
        long expiresInSeconds)
{}