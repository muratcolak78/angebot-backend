package com.angebot.backend.config;

import com.angebot.backend.dto.OfferCreateDTO;
import com.angebot.backend.dto.OfferDTO;
import com.angebot.backend.entity.Customer;
import com.angebot.backend.entity.Offer;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class OfferMapperTest {

    private final OfferMapper mapper = new OfferMapper();

    @Test
    void shouldMapOfferEntityToOfferDTO() {
        UUID userId = UUID.randomUUID();
        UUID customerId = UUID.randomUUID();
        Instant createdAt = Instant.parse("2026-03-11T10:15:30Z");
        Instant updatedAt = Instant.parse("2026-03-11T11:15:30Z");

        Offer offer = new Offer();
        offer.setId(100L);
        offer.setUserId(userId);
        offer.setCustomerId(customerId);
        offer.setWallM2(120.5);
        offer.setWallpaperM2(30.0);
        offer.setCeilingM2(18.5);
        offer.setWindowsM2(12.0);
        offer.setDoors(4);
        offer.setWallTotal(2400.0);
        offer.setWallpaperTotal(450.0);
        offer.setCeilingTotal(300.0);
        offer.setWindowDeduction(120.0);
        offer.setDoorDeduction(80.0);
        offer.setGrandTotal(2950.0);
        offer.setCreatedAt(createdAt);
        offer.setUpdatedAt(updatedAt);

        OfferDTO dto = mapper.toDTO(offer);

        assertNotNull(dto);
        assertEquals(100L, dto.getId());
        assertEquals(userId, dto.getUserId());
        assertEquals(customerId, dto.getCustomerId());
        assertEquals(120.5, dto.getWallM2());
        assertEquals(30.0, dto.getWallpaperM2());
        assertEquals(18.5, dto.getCeilingM2());
        assertEquals(12.0, dto.getWindowsM2());
        assertEquals(4, dto.getDoors());
        assertEquals(2400.0, dto.getWallTotal());
        assertEquals(450.0, dto.getWallpaperTotal());
        assertEquals(300.0, dto.getCeilingTotal());
        assertEquals(120.0, dto.getWindowDeduction());
        assertEquals(80.0, dto.getDoorDeduction());
        assertEquals(2950.0, dto.getGrandTotal());
        assertEquals(createdAt, dto.getCreatedAt());
        assertEquals(updatedAt, dto.getUpdatedAt());

        assertNull(dto.getCustomerFirstName());
        assertNull(dto.getCustomerLastName());
        assertNull(dto.getCustomerEmail());
        assertNull(dto.getCustomerPhone());
    }

    @Test
    void shouldMapOfferAndCustomerToOfferDTOWithCustomerFields() {
        UUID userId = UUID.randomUUID();
        UUID customerId = UUID.randomUUID();

        Offer offer = new Offer();
        offer.setId(200L);
        offer.setUserId(userId);
        offer.setCustomerId(customerId);
        offer.setWallM2(80.0);
        offer.setWallpaperM2(10.0);
        offer.setCeilingM2(12.0);
        offer.setWindowsM2(6.0);
        offer.setDoors(2);
        offer.setWallTotal(1600.0);
        offer.setWallpaperTotal(150.0);
        offer.setCeilingTotal(180.0);
        offer.setWindowDeduction(60.0);
        offer.setDoorDeduction(40.0);
        offer.setGrandTotal(1830.0);

        Customer customer = new Customer();
        customer.setId(customerId);
        customer.setFirstName("Max");
        customer.setLastName("Mustermann");
        customer.setEmail("max.mustermann@example.com");
        customer.setPhone("+49 123 456789");

        OfferDTO dto = mapper.toDTOWithCustomer(offer, customer);

        assertNotNull(dto);
        assertEquals(200L, dto.getId());
        assertEquals(userId, dto.getUserId());
        assertEquals(customerId, dto.getCustomerId());
        assertEquals(80.0, dto.getWallM2());
        assertEquals(10.0, dto.getWallpaperM2());
        assertEquals(12.0, dto.getCeilingM2());
        assertEquals(6.0, dto.getWindowsM2());
        assertEquals(2, dto.getDoors());
        assertEquals(1600.0, dto.getWallTotal());
        assertEquals(150.0, dto.getWallpaperTotal());
        assertEquals(180.0, dto.getCeilingTotal());
        assertEquals(60.0, dto.getWindowDeduction());
        assertEquals(40.0, dto.getDoorDeduction());
        assertEquals(1830.0, dto.getGrandTotal());

        assertEquals("Max", dto.getCustomerFirstName());
        assertEquals("Mustermann", dto.getCustomerLastName());
        assertEquals("max.mustermann@example.com", dto.getCustomerEmail());
        assertEquals("+49 123 456789", dto.getCustomerPhone());
    }

    @Test
    void shouldMapOfferToOfferDTOWhenCustomerIsNull() {
        UUID userId = UUID.randomUUID();
        UUID customerId = UUID.randomUUID();

        Offer offer = new Offer();
        offer.setId(300L);
        offer.setUserId(userId);
        offer.setCustomerId(customerId);
        offer.setWallM2(55.0);
        offer.setDoors(1);
        offer.setGrandTotal(999.99);

        OfferDTO dto = mapper.toDTOWithCustomer(offer, null);

        assertNotNull(dto);
        assertEquals(300L, dto.getId());
        assertEquals(userId, dto.getUserId());
        assertEquals(customerId, dto.getCustomerId());
        assertEquals(55.0, dto.getWallM2());
        assertEquals(1, dto.getDoors());
        assertEquals(999.99, dto.getGrandTotal());

        assertNull(dto.getCustomerFirstName());
        assertNull(dto.getCustomerLastName());
        assertNull(dto.getCustomerEmail());
        assertNull(dto.getCustomerPhone());
    }

    @Test
    void shouldMapOfferCreateDTOToOfferEntity() {
        UUID customerId = UUID.randomUUID();

        OfferCreateDTO dto = new OfferCreateDTO();
        dto.setCustomerId(customerId);
        dto.setWallM2(140.0);
        dto.setWallpaperM2(25.0);
        dto.setCeilingM2(20.0);
        dto.setWindowsM2(14.5);
        dto.setDoors(5);

        Offer entity = mapper.toEntity(dto);

        assertNotNull(entity);
        assertEquals(customerId, entity.getCustomerId());
        assertEquals(140.0, entity.getWallM2());
        assertEquals(25.0, entity.getWallpaperM2());
        assertEquals(20.0, entity.getCeilingM2());
        assertEquals(14.5, entity.getWindowsM2());
        assertEquals(5, entity.getDoors());

        assertNull(entity.getUserId());
        assertEquals(0.0, entity.getWallTotal());
        assertEquals(0.0, entity.getWallpaperTotal());
        assertEquals(0.0, entity.getCeilingTotal());
        assertEquals(0.0, entity.getWindowDeduction());
        assertEquals(0.0, entity.getDoorDeduction());
        assertEquals(0.0, entity.getGrandTotal());
    }
}