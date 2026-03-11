package com.angebot.backend.dto;

import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class OfferDTOTest {

    @Test
    void shouldSetAndGetAllFieldsCorrectly() {

        OfferDTO dto = new OfferDTO();

        Long id = 1L;
        UUID customerId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        Instant now = Instant.now();

        dto.setId(id);
        dto.setCustomerId(customerId);
        dto.setUserId(userId);

        dto.setCustomerFirstName("Mux");
        dto.setCustomerLastName("Col");
        dto.setCustomerEmail("murat@test.com");
        dto.setCustomerPhone("123456");

        dto.setWallM2(100);
        dto.setWallpaperM2(50);
        dto.setCeilingM2(40);
        dto.setWindowsM2(10);
        dto.setDoors(5);

        dto.setWallTotal(2000);
        dto.setWallpaperTotal(1000);
        dto.setCeilingTotal(800);

        dto.setWindowDeduction(200);
        dto.setDoorDeduction(100);
        dto.setGrandTotal(3500);

        dto.setCreatedAt(now);
        dto.setUpdatedAt(now);

        assertEquals(id, dto.getId());
        assertEquals(customerId, dto.getCustomerId());
        assertEquals(userId, dto.getUserId());

        assertEquals("Mux", dto.getCustomerFirstName());
        assertEquals("Col", dto.getCustomerLastName());
        assertEquals("murat@test.com", dto.getCustomerEmail());
        assertEquals("123456", dto.getCustomerPhone());

        assertEquals(100, dto.getWallM2());
        assertEquals(50, dto.getWallpaperM2());
        assertEquals(40, dto.getCeilingM2());
        assertEquals(10, dto.getWindowsM2());
        assertEquals(5, dto.getDoors());

        assertEquals(2000, dto.getWallTotal());
        assertEquals(1000, dto.getWallpaperTotal());
        assertEquals(800, dto.getCeilingTotal());

        assertEquals(200, dto.getWindowDeduction());
        assertEquals(100, dto.getDoorDeduction());
        assertEquals(3500, dto.getGrandTotal());

        assertEquals(now, dto.getCreatedAt());
        assertEquals(now, dto.getUpdatedAt());
    }

    @Test
    void shouldHaveDefaultValues() {

        OfferDTO dto = new OfferDTO();

        assertNull(dto.getId());
        assertNull(dto.getCustomerId());
        assertNull(dto.getUserId());

        assertEquals(0.0, dto.getWallM2());
        assertEquals(0.0, dto.getWallpaperM2());
        assertEquals(0.0, dto.getCeilingM2());
        assertEquals(0.0, dto.getWindowsM2());
        assertEquals(0, dto.getDoors());

        assertEquals(0.0, dto.getWallTotal());
        assertEquals(0.0, dto.getWallpaperTotal());
        assertEquals(0.0, dto.getCeilingTotal());
        assertEquals(0.0, dto.getWindowDeduction());
        assertEquals(0.0, dto.getDoorDeduction());
        assertEquals(0.0, dto.getGrandTotal());

        assertNull(dto.getCreatedAt());
        assertNull(dto.getUpdatedAt());
    }
}