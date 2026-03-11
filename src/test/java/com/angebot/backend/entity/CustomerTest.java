package com.angebot.backend.entity;

import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class CustomerTest {

    @Test
    void shouldSetAndGetAllFieldsCorrectly() {

        Customer customer = new Customer();

        UUID id = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        Instant now = Instant.now();

        customer.setId(id);
        customer.setUserId(userId);
        customer.setFirstName("Murat");
        customer.setLastName("Colak");
        customer.setEmail("murat@test.com");
        customer.setPhone("123456");

        customer.setHomeStreet("Main");
        customer.setHomeHouseNr("10");
        customer.setHomePlz("73240");
        customer.setHomeOrt("Wendlingen");

        customer.setWorkStreet("Office");
        customer.setWorkHouseNr("5");
        customer.setWorkPlz("70173");
        customer.setWorkOrt("Stuttgart");

        customer.setCreatedAt(now);
        customer.setUpdatedAt(now);

        assertEquals(id, customer.getId());
        assertEquals(userId, customer.getUserId());
        assertEquals("Murat", customer.getFirstName());
        assertEquals("Colak", customer.getLastName());
        assertEquals("murat@test.com", customer.getEmail());
        assertEquals("123456", customer.getPhone());

        assertEquals("Main", customer.getHomeStreet());
        assertEquals("10", customer.getHomeHouseNr());
        assertEquals("73240", customer.getHomePlz());
        assertEquals("Wendlingen", customer.getHomeOrt());

        assertEquals("Office", customer.getWorkStreet());
        assertEquals("5", customer.getWorkHouseNr());
        assertEquals("70173", customer.getWorkPlz());
        assertEquals("Stuttgart", customer.getWorkOrt());

        assertEquals(now, customer.getCreatedAt());
        assertEquals(now, customer.getUpdatedAt());
    }

    @Test
    void getHomeAddress_shouldReturnFormattedAddress() {

        Customer customer = new Customer();

        customer.setHomeStreet("Main");
        customer.setHomeHouseNr("10");
        customer.setHomePlz("73240");
        customer.setHomeOrt("Wendlingen");

        String address = customer.getHomeAddress();

        assertEquals("Main 10, 73240 Wendlingen", address);
    }

    @Test
    void getWorkAddress_shouldReturnFormattedAddress() {

        Customer customer = new Customer();

        customer.setWorkStreet("Office");
        customer.setWorkHouseNr("5");
        customer.setWorkPlz("70173");
        customer.setWorkOrt("Stuttgart");

        String address = customer.getWorkAddress();

        assertEquals("Office 5, 70173 Stuttgart", address);
    }

    @Test
    void getWorkAddress_shouldReturnEmpty_whenStreetIsNull() {

        Customer customer = new Customer();

        String address = customer.getWorkAddress();

        assertEquals("", address);
    }

    @Test
    void createdAt_shouldBeInitializedByDefault() {

        Customer customer = new Customer();

        assertNotNull(customer.getCreatedAt());
    }
}