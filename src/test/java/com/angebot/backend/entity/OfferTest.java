package com.angebot.backend.entity;

import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class OfferTest {

    @Test
    void shouldSetAndGetAllFieldsCorrectly() {

        Offer offer = new Offer();

        Long id = 1L;
        UUID userId = UUID.randomUUID();
        UUID customerId = UUID.randomUUID();
        Instant now = Instant.now();

        offer.setId(id);
        offer.setUserId(userId);
        offer.setCustomerId(customerId);

        offer.setWallM2(120);
        offer.setWallpaperM2(60);
        offer.setCeilingM2(30);
        offer.setWindowsM2(10);
        offer.setDoors(4);

        offer.setWallTotal(2400);
        offer.setWallpaperTotal(1200);
        offer.setCeilingTotal(600);
        offer.setWindowDeduction(100);
        offer.setDoorDeduction(50);
        offer.setGrandTotal(4050);

        offer.setCreatedAt(now);
        offer.setUpdatedAt(now);

        assertEquals(id, offer.getId());
        assertEquals(userId, offer.getUserId());
        assertEquals(customerId, offer.getCustomerId());

        assertEquals(120, offer.getWallM2());
        assertEquals(60, offer.getWallpaperM2());
        assertEquals(30, offer.getCeilingM2());
        assertEquals(10, offer.getWindowsM2());
        assertEquals(4, offer.getDoors());

        assertEquals(2400, offer.getWallTotal());
        assertEquals(1200, offer.getWallpaperTotal());
        assertEquals(600, offer.getCeilingTotal());
        assertEquals(100, offer.getWindowDeduction());
        assertEquals(50, offer.getDoorDeduction());
        assertEquals(4050, offer.getGrandTotal());

        assertEquals(now, offer.getCreatedAt());
        assertEquals(now, offer.getUpdatedAt());
    }

    @Test
    void shouldHaveDefaultValues() {

        Offer offer = new Offer();

        assertEquals(0.0, offer.getWallpaperM2());
        assertEquals(0.0, offer.getCeilingM2());
        assertEquals(0.0, offer.getWindowsM2());
        assertEquals(0, offer.getDoors());

        assertEquals(0.0, offer.getWallpaperTotal());
        assertEquals(0.0, offer.getCeilingTotal());
        assertEquals(0.0, offer.getWindowDeduction());
        assertEquals(0.0, offer.getDoorDeduction());

        assertNotNull(offer.getCreatedAt());
    }
}