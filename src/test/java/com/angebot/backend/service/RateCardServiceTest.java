package com.angebot.backend.service;

import com.angebot.backend.auth.jwt.JwtUtil;
import com.angebot.backend.config.RateCardMapper;
import com.angebot.backend.dto.RateCardCreateDTO;
import com.angebot.backend.dto.RateCardDTO;
import com.angebot.backend.dto.RateCardUpdateDTO;
import com.angebot.backend.entity.RateCard;
import com.angebot.backend.exception.NotFoundException;
import com.angebot.backend.repository.RateCardRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RateCardServiceTest {

    @Mock private RateCardRepository repo;
    @Mock private RateCardMapper mapper;
    @Mock private JwtUtil jwtUtil;

    @InjectMocks private RateCardService rateCardService;

    private UUID userId;
    private RateCard rateCard;
    private RateCardDTO rateCardDTO;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();

        rateCard = new RateCard();
        rateCard.setUserId(userId);
        rateCard.setWallM2Price(12.50);
        rateCard.setWallpaperM2Price(8.00);
        rateCard.setCeilingM2Price(10.00);
        rateCard.setWindowDeductionM2(5.00);
        rateCard.setDoorDeductionM2(4.00);
        rateCard.setCreatedAt(Instant.now());

        rateCardDTO = new RateCardDTO();
        rateCardDTO.setUserId(userId);
        rateCardDTO.setWallM2Price(12.50);
    }

    // ─── getMyRateCard ────────────────────────────────────────────────────────

    @Test
    @DisplayName("getMyRateCard - rate card döner")
    void shouldReturnRateCard() {
        when(jwtUtil.getCurrentUserId()).thenReturn(userId);
        when(repo.findByUserId(userId)).thenReturn(Optional.of(rateCard));
        when(mapper.toDTO(rateCard)).thenReturn(rateCardDTO);

        RateCardDTO result = rateCardService.getMyRateCard();

        assertThat(result.getWallM2Price()).isEqualTo(12.50);
    }

    @Test
    @DisplayName("getMyRateCard - rate card yoksa NotFoundException fırlatır")
    void shouldThrowIfRateCardNotFound() {
        when(jwtUtil.getCurrentUserId()).thenReturn(userId);
        when(repo.findByUserId(userId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> rateCardService.getMyRateCard())
                .isInstanceOf(NotFoundException.class);
    }

    // ─── createMyRateCard ─────────────────────────────────────────────────────

    @Test
    @DisplayName("createMyRateCard - yeni rate card oluşturur")
    void shouldCreateRateCard() {
        RateCardCreateDTO dto = new RateCardCreateDTO(12.50, 8.00, 5.00, 4.00, 10.00);

        when(jwtUtil.getCurrentUserId()).thenReturn(userId);
        when(repo.findByUserId(userId)).thenReturn(Optional.empty());
        when(repo.save(any())).thenReturn(rateCard);
        when(mapper.toDTO(rateCard)).thenReturn(rateCardDTO);

        RateCardDTO result = rateCardService.createMyRateCard(dto);

        assertThat(result.getWallM2Price()).isEqualTo(12.50);
        verify(repo).save(any());
    }

    @Test
    @DisplayName("createMyRateCard - zaten varsa IllegalStateException fırlatır")
    void shouldThrowIfRateCardAlreadyExists() {
        RateCardCreateDTO dto = new RateCardCreateDTO(12.50, 8.00, 5.00, 4.00, 10.00);

        when(jwtUtil.getCurrentUserId()).thenReturn(userId);
        when(repo.findByUserId(userId)).thenReturn(Optional.of(rateCard));

        assertThatThrownBy(() -> rateCardService.createMyRateCard(dto))
                .isInstanceOf(IllegalStateException.class);

        verify(repo, never()).save(any());
    }

    @Test
    @DisplayName("createMyRateCard - wallM2Price 0 ise IllegalArgumentException fırlatır")
    void shouldThrowIfWallPriceZero() {
        RateCardCreateDTO dto = new RateCardCreateDTO(0.0, 8.00, 5.00, 4.00, 10.00);

        when(jwtUtil.getCurrentUserId()).thenReturn(userId);
        when(repo.findByUserId(userId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> rateCardService.createMyRateCard(dto))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Wandpreis");
    }

    // ─── updateMyRateCard ─────────────────────────────────────────────────────

    @Test
    @DisplayName("updateMyRateCard - rate card günceller")
    void shouldUpdateRateCard() {
        RateCardUpdateDTO dto = new RateCardUpdateDTO();
        dto.setWallM2Price(15.00);

        when(jwtUtil.getCurrentUserId()).thenReturn(userId);
        when(repo.findByUserId(userId)).thenReturn(Optional.of(rateCard));
        when(repo.save(rateCard)).thenReturn(rateCard);
        when(mapper.toDTO(rateCard)).thenReturn(rateCardDTO);

        RateCardDTO result = rateCardService.updateMyRateCard(dto);

        assertThat(result).isNotNull();
        verify(repo).save(rateCard);
    }

    @Test
    @DisplayName("updateMyRateCard - rate card yoksa NotFoundException fırlatır")
    void shouldThrowIfRateCardNotFoundOnUpdate() {
        RateCardUpdateDTO dto = new RateCardUpdateDTO();
        dto.setWallM2Price(15.00);

        when(jwtUtil.getCurrentUserId()).thenReturn(userId);
        when(repo.findByUserId(userId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> rateCardService.updateMyRateCard(dto))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    @DisplayName("updateMyRateCard - negatif windowDeduction IllegalArgumentException fırlatır")
    void shouldThrowIfWindowDeductionNegative() {
        RateCardUpdateDTO dto = new RateCardUpdateDTO();
        dto.setWindowDeductionM2(-1.0);

        when(jwtUtil.getCurrentUserId()).thenReturn(userId);
        when(repo.findByUserId(userId)).thenReturn(Optional.of(rateCard));

        assertThatThrownBy(() -> rateCardService.updateMyRateCard(dto))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Fensterabzug");
    }

    // ─── deleteMyRateCard ─────────────────────────────────────────────────────

    @Test
    @DisplayName("deleteMyRateCard - rate card siler")
    void shouldDeleteRateCard() {
        when(jwtUtil.getCurrentUserId()).thenReturn(userId);
        when(repo.findByUserId(userId)).thenReturn(Optional.of(rateCard));

        rateCardService.deleteMyRateCard();

        verify(repo).delete(rateCard);
    }

    @Test
    @DisplayName("deleteMyRateCard - rate card yoksa NotFoundException fırlatır")
    void shouldThrowIfRateCardNotFoundOnDelete() {
        when(jwtUtil.getCurrentUserId()).thenReturn(userId);
        when(repo.findByUserId(userId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> rateCardService.deleteMyRateCard())
                .isInstanceOf(NotFoundException.class);
    }
}