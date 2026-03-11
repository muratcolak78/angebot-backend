package com.angebot.backend.controller;

import com.angebot.backend.dto.CustomerCreateDTO;
import com.angebot.backend.dto.CustomerDTO;
import com.angebot.backend.dto.CustomerUpdateDTO;
import com.angebot.backend.service.CustomerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CustomerControllerTest {

    @Mock
    private CustomerService service;

    @InjectMocks
    private CustomerController controller;

    private CustomerDTO buildDTO(UUID id) {
        CustomerDTO dto = new CustomerDTO();
        dto.setId(id);
        dto.setFirstName("Max");
        dto.setLastName("Mustermann");
        dto.setEmail("max@test.de");
        return dto;
    }

    @Test
    @DisplayName("POST /api/customers - müşteri oluşturur")
    void shouldCreateCustomer() {
        UUID id = UUID.randomUUID();
        CustomerCreateDTO createDTO = new CustomerCreateDTO(
                "Max", "Mustermann", "max@test.de", "0711123",
                null, null, null, null, null, null, null, null
        );

        when(service.createCustomer(any())).thenReturn(buildDTO(id));

        CustomerDTO result = controller.create(createDTO);

        assertThat(result.getFirstName()).isEqualTo("Max");
        assertThat(result.getEmail()).isEqualTo("max@test.de");
    }

    @Test
    @DisplayName("GET /api/customers/me - müşteri listesini döner")
    void shouldReturnMyCustomers() {
        UUID id = UUID.randomUUID();
        when(service.getMyCustomers()).thenReturn(List.of(buildDTO(id)));

        List<CustomerDTO> result = controller.getMyCustomers();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getFirstName()).isEqualTo("Max");
    }

    @Test
    @DisplayName("GET /api/customers/{id} - müşteriyi döner")
    void shouldReturnCustomer() {
        UUID id = UUID.randomUUID();
        when(service.getCustomer(id)).thenReturn(buildDTO(id));

        CustomerDTO result = controller.getCustomer(id);

        assertThat(result.getEmail()).isEqualTo("max@test.de");
    }

    @Test
    @DisplayName("PUT /api/customers/{id} - müşteriyi günceller")
    void shouldUpdateCustomer() {
        UUID id = UUID.randomUUID();
        CustomerUpdateDTO updateDTO = new CustomerUpdateDTO(
                "Anna", null, null, null,
                null, null, null, null,
                null, null, null, null
        );

        CustomerDTO updated = buildDTO(id);
        updated.setFirstName("Anna");

        when(service.updateCustomer(eq(id), any())).thenReturn(updated);

        CustomerDTO result = controller.updateCustomer(id, updateDTO);

        assertThat(result.getFirstName()).isEqualTo("Anna");
    }

    @Test
    @DisplayName("DELETE /api/customers/{id} - 204 döner")
    void shouldDeleteCustomer() {
        UUID id = UUID.randomUUID();
        doNothing().when(service).deleteCustomer(id);

        ResponseEntity<Void> result = controller.deleteCustomer(id);

        assertThat(result.getStatusCode().value()).isEqualTo(204);
    }
}