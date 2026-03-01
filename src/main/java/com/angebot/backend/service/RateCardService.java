package com.angebot.backend.service;

import com.angebot.backend.auth.jwt.JwtUtil;
import com.angebot.backend.config.RateCardMapper;
import com.angebot.backend.dto.RateCardCreateDTO;
import com.angebot.backend.dto.RateCardDTO;
import com.angebot.backend.dto.RateCardUpdateDTO;
import com.angebot.backend.entity.RateCard;
import com.angebot.backend.exception.NotFoundException; // Custom exception
import com.angebot.backend.repository.RateCardRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class RateCardService {
    private final RateCardRepository repo;
    private final RateCardMapper mapper;
    private final JwtUtil jwtUtil;

    // GET /me - RateCard varsa döndür, yoksa 404 fırlat
    public RateCardDTO getMyRateCard() {
        UUID userId = jwtUtil.getCurrentUserId();

        return repo.findByUserId(userId)
                .map(mapper::toDTO)
                .orElseThrow(() -> new NotFoundException("Preiskarte nicht gefunden. Bitte erstellen Sie zuerst eine Preiskarte."));
    }

    // POST /me - Yeni RateCard oluştur (sadece yoksa)
    public RateCardDTO createMyRateCard(@Valid RateCardCreateDTO createDTO) {
        UUID userId = jwtUtil.getCurrentUserId();

        // Var mı kontrol et - varsa hata fırlat
        Optional<RateCard> existing = repo.findByUserId(userId);
        if (existing.isPresent()) {
            throw new IllegalStateException("Preiskarte existiert bereits. Bitte verwenden Sie PUT zum Aktualisieren.");
        }

        // Yeni oluştur
        RateCard newCard = new RateCard();
        BeanUtils.copyProperties(createDTO, newCard);
        newCard.setUserId(userId);
        newCard.setCreatedAt(Instant.now());

        // Girişleri validate et
        validateRateCard(newCard);

        RateCard saved = repo.save(newCard);
        log.info("Neue Preiskarte erstellt für User: {}", userId);

        return mapper.toDTO(saved);
    }

    // PUT /me - Varolan RateCard'ı güncelle
    public RateCardDTO updateMyRateCard(@Valid RateCardUpdateDTO dto) {
        UUID userId = jwtUtil.getCurrentUserId();

        RateCard rc = repo.findByUserId(userId)
                .orElseThrow(() -> new NotFoundException("Preiskarte nicht gefunden. Bitte erstellen Sie zuerst eine Preiskarte."));

        // Sadece gönderilen alanları güncelle
        if (dto.getWallM2Price() != null) {
            rc.setWallM2Price(dto.getWallM2Price());
        }
        if (dto.getWallpaperM2Price() != null) {
            rc.setWallpaperM2Price(dto.getWallpaperM2Price());
        }
        if (dto.getCeilingM2Price() != null) {
            rc.setCeilingM2Price(dto.getCeilingM2Price());
        }
        if (dto.getWindowDeductionM2() != null) {
            rc.setWindowDeductionM2(dto.getWindowDeductionM2());
        }
        if (dto.getDoorDeductionM2() != null) {
            rc.setDoorDeductionM2(dto.getDoorDeductionM2());
        }

        rc.setUpdatedAt(Instant.now());

        // Güncel değerleri validate et
        validateRateCard(rc);

        RateCard saved = repo.save(rc);
        log.info("Preiskarte aktualisiert für User: {}", userId);

        return mapper.toDTO(saved);
    }

    // DELETE /me - RateCard'ı sil (opsiyonel)
    public void deleteMyRateCard() {
        UUID userId = jwtUtil.getCurrentUserId();

        RateCard rc = repo.findByUserId(userId)
                .orElseThrow(() -> new NotFoundException("Preiskarte nicht gefunden"));

        repo.delete(rc);
        log.info("Preiskarte gelöscht für User: {}", userId);
    }

    // Validasyon metodu
    private void validateRateCard(RateCard rc) {
        if (rc.getWallM2Price() <= 0) {
            throw new IllegalArgumentException("Wandpreis muss größer als 0 sein");
        }
        if (rc.getWallpaperM2Price() <= 0) {
            throw new IllegalArgumentException("Tapetenpreis muss größer als 0 sein");
        }
        if (rc.getCeilingM2Price() <= 0) {
            throw new IllegalArgumentException("Deckenpreis muss größer als 0 sein");
        }
        if (rc.getWindowDeductionM2() < 0) {
            throw new IllegalArgumentException("Fensterabzug kann nicht negativ sein");
        }
        if (rc.getDoorDeductionM2() < 0) {
            throw new IllegalArgumentException("Türabzug kann nicht negativ sein");
        }
    }
}