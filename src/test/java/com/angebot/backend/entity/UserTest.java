package com.angebot.backend.entity;

import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class UserTest {

    @Test
    void shouldSetAndGetAllFieldsCorrectly() {

        User user = new User();

        UUID id = UUID.randomUUID();
        Instant now = Instant.now();

        user.setId(id);
        user.setEmail("murat@test.com");
        user.setPasswordHash("hashedPassword123");
        user.setCreatedAt(now);

        assertEquals(id, user.getId());
        assertEquals("murat@test.com", user.getEmail());
        assertEquals("hashedPassword123", user.getPasswordHash());
        assertEquals(now, user.getCreatedAt());
    }

    @Test
    void createdAt_shouldBeInitializedByDefault() {

        User user = new User();

        assertNotNull(user.getCreatedAt());
    }
}