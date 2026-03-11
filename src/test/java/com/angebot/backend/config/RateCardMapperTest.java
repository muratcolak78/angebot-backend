package com.angebot.backend.config;

import com.angebot.backend.dto.RateCardDTO;
import com.angebot.backend.dto.RateCardUpdateDTO;
import com.angebot.backend.entity.RateCard;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class RateCardMapperTest {

    private RateCardMapper rateCardMapper;

    @BeforeEach
    void setUp() {
        rateCardMapper = new RateCardMapper();
    }

    @Test
    void toDTO_shouldMapAllFields_whenUpdatedAtIsNotNull() {
        UUID id = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        Instant createdAt = Instant.parse("2026-03-11T10:15:30Z");
        Instant updatedAt = Instant.parse("2026-03-11T11:15:30Z");

        RateCard entity = new RateCard();
        entity.setId(id);
        entity.setUserId(userId);
        entity.setWallM2Price(12.5);
        entity.setWallpaperM2Price(8.75);
        entity.setWindowDeductionM2(1.2);
        entity.setDoorDeductionM2(2.3);
        entity.setCeilingM2Price(9.9);
        entity.setCreatedAt(createdAt);
        entity.setUpdatedAt(updatedAt);

        RateCardDTO dto = rateCardMapper.toDTO(entity);

        assertNotNull(dto);
        assertEquals(id, dto.getId());
        assertEquals(userId, dto.getUserId());
        assertEquals(12.5, dto.getWallM2Price());
        assertEquals(8.75, dto.getWallpaperM2Price());
        assertEquals(1.2, dto.getWindowDeductionM2());
        assertEquals(2.3, dto.getDoorDeductionM2());
        assertEquals(9.9, dto.getCeilingM2Price());
        assertEquals(createdAt.toString(), dto.getCreatedAt());
        assertEquals(updatedAt.toString(), dto.getUpdatedAt());
    }

    @Test
    void toDTO_shouldSetUpdatedAtNull_whenEntityUpdatedAtIsNull() {
        UUID id = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        Instant createdAt = Instant.parse("2026-03-11T10:15:30Z");

        RateCard entity = new RateCard();
        entity.setId(id);
        entity.setUserId(userId);
        entity.setWallM2Price(15.0);
        entity.setWallpaperM2Price(7.5);
        entity.setWindowDeductionM2(1.0);
        entity.setDoorDeductionM2(2.0);
        entity.setCeilingM2Price(5.5);
        entity.setCreatedAt(createdAt);
        entity.setUpdatedAt(null);

        RateCardDTO dto = rateCardMapper.toDTO(entity);

        assertNotNull(dto);
        assertEquals(id, dto.getId());
        assertEquals(userId, dto.getUserId());
        assertEquals(15.0, dto.getWallM2Price());
        assertEquals(7.5, dto.getWallpaperM2Price());
        assertEquals(1.0, dto.getWindowDeductionM2());
        assertEquals(2.0, dto.getDoorDeductionM2());
        assertEquals(5.5, dto.getCeilingM2Price());
        assertEquals(createdAt.toString(), dto.getCreatedAt());
        assertNull(dto.getUpdatedAt());
    }

    @Test
    void updateEntity_shouldUpdateAllFields_whenAllDtoFieldsAreNotNull() {
        RateCard entity = new RateCard();
        entity.setWallM2Price(10.0);
        entity.setWallpaperM2Price(20.0);
        entity.setWindowDeductionM2(1.0);
        entity.setDoorDeductionM2(2.0);
        entity.setCeilingM2Price(30.0);

        RateCardUpdateDTO dto = new RateCardUpdateDTO();
        dto.setWallM2Price(11.0);
        dto.setWallpaperM2Price(21.0);
        dto.setWindowDeductionM2(1.5);
        dto.setDoorDeductionM2(2.5);
        dto.setCeilingM2Price(31.0);

        rateCardMapper.updateEntity(dto, entity);

        assertEquals(11.0, entity.getWallM2Price());
        assertEquals(21.0, entity.getWallpaperM2Price());
        assertEquals(1.5, entity.getWindowDeductionM2());
        assertEquals(2.5, entity.getDoorDeductionM2());
        assertEquals(31.0, entity.getCeilingM2Price());
    }

    @Test
    void updateEntity_shouldUpdateOnlyNonNullFields_whenDtoContainsPartialData() {
        RateCard entity = new RateCard();
        entity.setWallM2Price(10.0);
        entity.setWallpaperM2Price(20.0);
        entity.setWindowDeductionM2(1.0);
        entity.setDoorDeductionM2(2.0);
        entity.setCeilingM2Price(30.0);

        RateCardUpdateDTO dto = new RateCardUpdateDTO();
        dto.setWallM2Price(99.0);
        dto.setWallpaperM2Price(null);
        dto.setWindowDeductionM2(null);
        dto.setDoorDeductionM2(5.0);
        dto.setCeilingM2Price(null);

        rateCardMapper.updateEntity(dto, entity);

        assertEquals(99.0, entity.getWallM2Price());
        assertEquals(20.0, entity.getWallpaperM2Price());
        assertEquals(1.0, entity.getWindowDeductionM2());
        assertEquals(5.0, entity.getDoorDeductionM2());
        assertEquals(30.0, entity.getCeilingM2Price());
    }

    @Test
    void updateEntity_shouldNotChangeAnything_whenAllDtoFieldsAreNull() {
        RateCard entity = new RateCard();
        entity.setWallM2Price(10.0);
        entity.setWallpaperM2Price(20.0);
        entity.setWindowDeductionM2(1.0);
        entity.setDoorDeductionM2(2.0);
        entity.setCeilingM2Price(30.0);

        RateCardUpdateDTO dto = new RateCardUpdateDTO();

        rateCardMapper.updateEntity(dto, entity);

        assertEquals(10.0, entity.getWallM2Price());
        assertEquals(20.0, entity.getWallpaperM2Price());
        assertEquals(1.0, entity.getWindowDeductionM2());
        assertEquals(2.0, entity.getDoorDeductionM2());
        assertEquals(30.0, entity.getCeilingM2Price());
    }
}