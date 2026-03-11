package com.angebot.backend.entity;

import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class AssetTest {

    @Test
    void shouldSetAndGetAllFieldsCorrectly() {

        Asset asset = new Asset();

        UUID id = UUID.randomUUID();
        Instant now = Instant.now();
        byte[] data = "test-data".getBytes();

        asset.setId(id);
        asset.setContentType("image/png");
        asset.setCreatedAt(now);
        asset.setData(data);
        asset.setFileName("logo.png");

        assertEquals(id, asset.getId());
        assertEquals("image/png", asset.getContentType());
        assertEquals(now, asset.getCreatedAt());
        assertArrayEquals(data, asset.getData());
        assertEquals("logo.png", asset.getFileName());
    }

    @Test
    void createdAt_shouldBeInitializedByDefault() {

        Asset asset = new Asset();

        assertNotNull(asset.getCreatedAt());
    }

    @Test
    void shouldHandleBinaryDataCorrectly() {

        Asset asset = new Asset();

        byte[] data = new byte[]{1,2,3,4};

        asset.setData(data);

        assertArrayEquals(data, asset.getData());
    }
}