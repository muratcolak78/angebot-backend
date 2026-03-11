package com.angebot.backend.config;

import com.angebot.backend.dto.CustomerCreateDTO;
import com.angebot.backend.dto.CustomerDTO;
import com.angebot.backend.dto.CustomerUpdateDTO;
import com.angebot.backend.entity.Customer;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

class CustomerMapperTest {

    private final CustomerMapper mapper = new CustomerMapper();

    @Test
    void shouldConvertEntityToDTO() {

        Customer entity = new Customer();
        entity.setFirstName("Murat");
        entity.setLastName("Colak");
        entity.setEmail("murat@test.com");
        entity.setPhone("12345");

        CustomerDTO dto = mapper.toDTO(entity);

        assertNotNull(dto);
        assertEquals("Murat", dto.getFirstName());
        assertEquals("Colak", dto.getLastName());
        assertEquals("murat@test.com", dto.getEmail());
        assertEquals("12345", dto.getPhone());
    }

    @Test
    void shouldConvertCreateDTOToEntity() {

        CustomerCreateDTO dto = new CustomerCreateDTO(
                "Murat",
                "Colak",
                "murat@test.com",
                "12345",
                "Street",
                "10",
                "12345",
                "Stuttgart",
                "workstreet",
                "45",
                "34556",
                "test");


        Customer entity = mapper.toEntity(dto);

        assertNotNull(entity);
        assertEquals("Murat", entity.getFirstName());
        assertEquals("Colak", entity.getLastName());
        assertEquals("murat@test.com", entity.getEmail());
        assertEquals("12345", entity.getPhone());
    }

    @Test
    void shouldUpdateEntityFields() {

        Customer entity = new Customer();
        entity.setFirstName("Old");
        entity.setLastName("User");

        CustomerUpdateDTO dto = new CustomerUpdateDTO(
                "New",
                "User",
                "new@test.com",
                "555",
                "Street",
                "5",
                "11111",
                "Berlin",
                null,
                null,
                null,
                null
        );

        mapper.updateEntity(dto, entity);

        assertEquals("New", entity.getFirstName());
        assertEquals("User", entity.getLastName());
        assertEquals("new@test.com", entity.getEmail());
        assertEquals("555", entity.getPhone());
        assertEquals("Street", entity.getHomeStreet());
        assertEquals("5", entity.getHomeHouseNr());
        assertEquals("11111", entity.getHomePlz());
        assertEquals("Berlin", entity.getHomeOrt());

        assertNotNull(entity.getUpdatedAt());
    }

    @Test
    void shouldNotOverwriteFieldsWhenDTOValueIsNull() {

        Customer entity = new Customer();
        entity.setFirstName("Murat");
        entity.setLastName("Colak");

        CustomerUpdateDTO dto = new CustomerUpdateDTO(
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null
        );

        mapper.updateEntity(dto, entity);

        assertEquals("Murat", entity.getFirstName());
        assertEquals("Colak", entity.getLastName());
    }
}