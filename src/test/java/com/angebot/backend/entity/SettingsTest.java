package com.angebot.backend.entity;

import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class SettingsTest {

    @Test
    void shouldSetAndGetAllFieldsCorrectly() {

        Settings settings = new Settings();

        UUID id = UUID.randomUUID();
        UUID logoId = UUID.randomUUID();
        UUID signatureId = UUID.randomUUID();
        Instant now = Instant.now();

        settings.setId(id);
        settings.setFirstName("Mux");
        settings.setLastName("Col");
        settings.setCompanyName("MC Software");
        settings.setPhone("123456");
        settings.setEmail("murat@test.com");
        settings.setTaxNumber("DE123456");

        settings.setStreet("Bahnhofstraße");
        settings.setHouseNr("12");
        settings.setPlz("70173");
        settings.setOrt("Stuttgart");

        settings.setLogoAssetId(logoId);
        settings.setSignatureAssetId(signatureId);

        settings.setUpdatedAt(now);

        assertEquals(id, settings.getId());
        assertEquals("Mux", settings.getFirstName());
        assertEquals("Col", settings.getLastName());
        assertEquals("MC Software", settings.getCompanyName());
        assertEquals("123456", settings.getPhone());
        assertEquals("murat@test.com", settings.getEmail());
        assertEquals("DE123456", settings.getTaxNumber());

        assertEquals("Bahnhofstraße", settings.getStreet());
        assertEquals("12", settings.getHouseNr());
        assertEquals("70173", settings.getPlz());
        assertEquals("Stuttgart", settings.getOrt());

        assertEquals(logoId, settings.getLogoAssetId());
        assertEquals(signatureId, settings.getSignatureAssetId());

        assertEquals(now, settings.getUpdatedAt());
    }

    @Test
    void getFullAddress_shouldReturnFormattedAddress() {

        Settings settings = new Settings();

        settings.setStreet("Bahnhofstraße");
        settings.setHouseNr("12");
        settings.setPlz("70173");
        settings.setOrt("Stuttgart");

        String address = settings.getFullAddress();

        assertEquals("Bahnhofstraße 12, 70173 Stuttgart", address);
    }

    @Test
    void getFullName_shouldReturnFullName() {

        Settings settings = new Settings();

        settings.setFirstName("Mux");
        settings.setLastName("Col");

        String name = settings.getFullName();

        assertEquals("Mux Col", name);
    }

    @Test
    void updatedAt_shouldBeInitializedByDefault() {

        Settings settings = new Settings();

        assertNotNull(settings.getUpdatedAt());
    }
}