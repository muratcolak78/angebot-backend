package com.angebot.backend.controller;

import com.angebot.backend.dto.RateCardCreateDTO;
import com.angebot.backend.dto.RateCardDTO;
import com.angebot.backend.dto.RateCardUpdateDTO;
import com.angebot.backend.service.RateCardService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RateCardControllerTest {

    @Mock
    private RateCardService service;

    @InjectMocks
    private RateCardController controller;

    private RateCardDTO buildDTO() {
        RateCardDTO dto = new RateCardDTO();
        dto.setWallM2Price(12.50);
        dto.setWallpaperM2Price(8.00);
        return dto;
    }

    @Test
    @DisplayName("GET /api/ratecard/me - rate card döner")
    void shouldReturnRateCard() {
        when(service.getMyRateCard()).thenReturn(buildDTO());

        ResponseEntity<RateCardDTO> result = controller.getMyRateCard();

        assertThat(result.getStatusCode().value()).isEqualTo(200);
        assertThat(result.getBody().getWallM2Price()).isEqualTo(12.50);
    }

    @Test
    @DisplayName("POST /api/ratecard/me - rate card oluşturur")
    void shouldCreateRateCard() {
        when(service.createMyRateCard(any())).thenReturn(buildDTO());

        ResponseEntity<RateCardDTO> result = controller.createMyRateCard(new RateCardCreateDTO(10d,20d,30d,40d,50d));

        assertThat(result.getStatusCode().value()).isEqualTo(201);
        assertThat(result.getBody().getWallM2Price()).isEqualTo(12.50);
    }

    @Test
    @DisplayName("PUT /api/ratecard/me - rate card günceller")
    void shouldUpdateRateCard() {
        when(service.updateMyRateCard(any())).thenReturn(buildDTO());

        ResponseEntity<RateCardDTO> result = controller.updateMyRateCard(new RateCardUpdateDTO());

        assertThat(result.getStatusCode().value()).isEqualTo(200);
        assertThat(result.getBody().getWallM2Price()).isEqualTo(12.50);
    }

    @Test
    @DisplayName("DELETE /api/ratecard/me - 204 döner")
    void shouldDeleteRateCard() {
        doNothing().when(service).deleteMyRateCard();

        ResponseEntity<Void> result = controller.deleteMyRateCard();

        assertThat(result.getStatusCode().value()).isEqualTo(204);
    }
}