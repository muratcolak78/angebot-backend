package com.angebot.backend.config;

import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;

class PasswordConfigTest {

    private final PasswordConfig config = new PasswordConfig();

    @Test
    void shouldCreateBCryptPasswordEncoder() {

        PasswordEncoder encoder = config.passwordEncoder();

        assertNotNull(encoder);

        String rawPassword = "MaxMustermann123";
        String encoded = encoder.encode(rawPassword);

        assertNotNull(encoded);
        assertNotEquals(rawPassword, encoded);

        assertTrue(encoder.matches(rawPassword, encoded));
    }
}