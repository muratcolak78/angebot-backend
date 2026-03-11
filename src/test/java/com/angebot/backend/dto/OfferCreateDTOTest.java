package com.angebot.backend.dto;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class OfferCreateDTOTest {

    @Test
    void shouldSetAndGetAllFieldsCorrectly() {

        UUID customerId = UUID.randomUUID();

        OfferCreateDTO dto = new OfferCreateDTO();

        dto.setCustomerId(customerId);
        dto.setWallM2(120.5);
        dto.setWallpaperM2(80.0);
        dto.setCeilingM2(60.5);
        dto.setWindowsM2(10.2);
        dto.setDoors(5);

        assertEquals(customerId, dto.getCustomerId());
        assertEquals(120.5, dto.getWallM2());
        assertEquals(80.0, dto.getWallpaperM2());
        assertEquals(60.5, dto.getCeilingM2());
        assertEquals(10.2, dto.getWindowsM2());
        assertEquals(5, dto.getDoors());
    }

    @Test
    void shouldAllowDefaultValues() {

        OfferCreateDTO dto = new OfferCreateDTO();

        assertNull(dto.getCustomerId());
        assertEquals(0.0, dto.getWallM2());
        assertEquals(0.0, dto.getWallpaperM2());
        assertEquals(0.0, dto.getCeilingM2());
        assertEquals(0.0, dto.getWindowsM2());
        assertEquals(0, dto.getDoors());
    }
}