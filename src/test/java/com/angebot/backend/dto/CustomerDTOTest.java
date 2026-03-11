package com.angebot.backend.dto;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class CustomerDTOTest {

    @Test
    void shouldSetAndGetAllFieldsCorrectly() {

        UUID id = UUID.randomUUID();

        CustomerDTO dto = new CustomerDTO();

        dto.setId(id);
        dto.setFirstName("Mux");
        dto.setLastName("Col");
        dto.setEmail("murat@test.com");
        dto.setPhone("123456");

        dto.setHomeStreet("Main Street");
        dto.setHomeHouseNr("10");
        dto.setHomePlz("73240");
        dto.setHomeOrt("Wendlingen");

        dto.setWorkStreet("Office Street");
        dto.setWorkHouseNr("5");
        dto.setWorkPlz("70173");
        dto.setWorkOrt("Stuttgart");

        assertEquals(id, dto.getId());
        assertEquals("Mux", dto.getFirstName());
        assertEquals("Col", dto.getLastName());
        assertEquals("murat@test.com", dto.getEmail());
        assertEquals("123456", dto.getPhone());

        assertEquals("Main Street", dto.getHomeStreet());
        assertEquals("10", dto.getHomeHouseNr());
        assertEquals("73240", dto.getHomePlz());
        assertEquals("Wendlingen", dto.getHomeOrt());

        assertEquals("Office Street", dto.getWorkStreet());
        assertEquals("5", dto.getWorkHouseNr());
        assertEquals("70173", dto.getWorkPlz());
        assertEquals("Stuttgart", dto.getWorkOrt());
    }
}