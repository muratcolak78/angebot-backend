package com.angebot.backend.entity;

import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class RateCardTest {

    @Test
    void shouldSetAndGetAllFieldsCorrectly() {
        UUID id = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        Instant createdAt = Instant.parse("2026-03-11T10:15:30Z");
        Instant updatedAt = Instant.parse("2026-03-11T11:15:30Z");

        RateCard entity = new RateCard();
        entity.setId(id);
        entity.setUserId(userId);
        entity.setWallM2Price(10.0);
        entity.setWallpaperM2Price(20.0);
        entity.setWindowDeductionM2(1.0);
        entity.setDoorDeductionM2(2.0);
        entity.setCeilingM2Price(30.0);
        entity.setCreatedAt(createdAt);
        entity.setUpdatedAt(updatedAt);

        assertEquals(id, entity.getId());
        assertEquals(userId, entity.getUserId());
        assertEquals(10.0, entity.getWallM2Price());
        assertEquals(20.0, entity.getWallpaperM2Price());
        assertEquals(1.0, entity.getWindowDeductionM2());
        assertEquals(2.0, entity.getDoorDeductionM2());
        assertEquals(30.0, entity.getCeilingM2Price());
        assertEquals(createdAt, entity.getCreatedAt());
        assertEquals(updatedAt, entity.getUpdatedAt());
    }

    @Test
    void createdAt_shouldBeInitializedByDefault() {
        RateCard entity = new RateCard();

        assertNotNull(entity.getCreatedAt());
    }
}