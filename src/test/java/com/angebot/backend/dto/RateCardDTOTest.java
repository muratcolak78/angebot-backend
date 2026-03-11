package com.angebot.backend.dto;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class RateCardDTOTest {

    @Test
    void shouldSetAndGetAllFieldsCorrectly() {
        UUID id = UUID.randomUUID();
        UUID userId = UUID.randomUUID();

        RateCardDTO dto = new RateCardDTO();
        dto.setId(id);
        dto.setUserId(userId);
        dto.setWallM2Price(10.5);
        dto.setWallpaperM2Price(20.5);
        dto.setWindowDeductionM2(1.1);
        dto.setDoorDeductionM2(2.2);
        dto.setCeilingM2Price(30.3);
        dto.setCreatedAt("2026-03-11T10:15:30Z");
        dto.setUpdatedAt("2026-03-11T11:15:30Z");

        assertEquals(id, dto.getId());
        assertEquals(userId, dto.getUserId());
        assertEquals(10.5, dto.getWallM2Price());
        assertEquals(20.5, dto.getWallpaperM2Price());
        assertEquals(1.1, dto.getWindowDeductionM2());
        assertEquals(2.2, dto.getDoorDeductionM2());
        assertEquals(30.3, dto.getCeilingM2Price());
        assertEquals("2026-03-11T10:15:30Z", dto.getCreatedAt());
        assertEquals("2026-03-11T11:15:30Z", dto.getUpdatedAt());
    }
}