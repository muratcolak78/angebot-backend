package com.angebot.backend.controller;

import com.angebot.backend.dto.OfferCreateDTO;
import com.angebot.backend.dto.OfferDTO;
import com.angebot.backend.service.OfferService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OfferControllerTest {

    @Mock
    private OfferService service;

    @InjectMocks
    private OfferController controller;

    private OfferDTO buildDTO(Long id) {
        OfferDTO dto = new OfferDTO();
        dto.setId(id);
        return dto;
    }

    @Test
    @DisplayName("POST /api/offers - teklif oluşturur")
    void shouldCreateOffer() {
        OfferCreateDTO createDTO = new OfferCreateDTO();
        when(service.createOffer(any())).thenReturn(buildDTO(1L));

        ResponseEntity<OfferDTO> result = controller.createOffer(createDTO);

        assertThat(result.getStatusCode().value()).isEqualTo(200);
        assertThat(result.getBody().getId()).isEqualTo(1L);
    }

    @Test
    @DisplayName("GET /api/offers/me - teklif listesini döner")
    void shouldReturnMyOffers() {
        when(service.getMyOffers()).thenReturn(List.of(buildDTO(1L), buildDTO(2L)));

        List<OfferDTO> result = controller.getMyOffers();

        assertThat(result).hasSize(2);
    }

    @Test
    @DisplayName("GET /api/offers/{id} - teklifi döner")
    void shouldReturnOffer() {
        when(service.getOffer(1L)).thenReturn(buildDTO(1L));

        OfferDTO result = controller.getOffer(1L);

        assertThat(result.getId()).isEqualTo(1L);
    }

    @Test
    @DisplayName("PUT /api/offers/{id} - teklifi günceller")
    void shouldUpdateOffer() {
        OfferCreateDTO updateDTO = new OfferCreateDTO();
        when(service.updateOffer(eq(1L), any())).thenReturn(buildDTO(1L));

        OfferDTO result = controller.updateOffer(1L, updateDTO);

        assertThat(result.getId()).isEqualTo(1L);
    }

    @Test
    @DisplayName("DELETE /api/offers/{id} - 204 döner")
    void shouldDeleteOffer() {
        doNothing().when(service).deleteOffer(1L);

        ResponseEntity<Void> result = controller.deleteOffer(1L);

        assertThat(result.getStatusCode().value()).isEqualTo(204);
    }
}