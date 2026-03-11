package com.angebot.backend.dto;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RateCardUpdateDTOTest {

    @Test
    void shouldSetAndGetAllFieldsCorrectly() {

        RateCardUpdateDTO dto = new RateCardUpdateDTO();

        dto.setWallM2Price(15.5);
        dto.setWallpaperM2Price(12.0);
        dto.setWindowDeductionM2(2.0);
        dto.setDoorDeductionM2(1.5);
        dto.setCeilingM2Price(10.0);

        assertEquals(15.5, dto.getWallM2Price());
        assertEquals(12.0, dto.getWallpaperM2Price());
        assertEquals(2.0, dto.getWindowDeductionM2());
        assertEquals(1.5, dto.getDoorDeductionM2());
        assertEquals(10.0, dto.getCeilingM2Price());
    }

    @Test
    void shouldAllowNullValues() {

        RateCardUpdateDTO dto = new RateCardUpdateDTO();

        assertNull(dto.getWallM2Price());
        assertNull(dto.getWallpaperM2Price());
        assertNull(dto.getWindowDeductionM2());
        assertNull(dto.getDoorDeductionM2());
        assertNull(dto.getCeilingM2Price());
    }
}