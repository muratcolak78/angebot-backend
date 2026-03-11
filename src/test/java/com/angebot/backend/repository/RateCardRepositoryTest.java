package com.angebot.backend.repository;

import com.angebot.backend.entity.RateCard;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class RateCardRepositoryTest {

    @Autowired
    private RateCardRepository rateCardRepository;

    private RateCard buildRateCard(UUID userId) {
        RateCard rc = new RateCard();
        rc.setUserId(userId);
        rc.setWallM2Price(12.50);
        rc.setWallpaperM2Price(8.00);
        rc.setWindowDeductionM2(5.00);
        rc.setDoorDeductionM2(4.00);
        rc.setCeilingM2Price(10.00);
        return rc;
    }

    @Test
    @DisplayName("findByUserId - kullanıcıya ait rate card döner")
    void shouldFindRateCardByUserId() {
        UUID userId = UUID.randomUUID();
        rateCardRepository.save(buildRateCard(userId));

        Optional<RateCard> result = rateCardRepository.findByUserId(userId);

        assertThat(result).isPresent();
        assertThat(result.get().getUserId()).isEqualTo(userId);
        assertThat(result.get().getWallM2Price()).isEqualTo(12.50);
    }

    @Test
    @DisplayName("findByUserId - yanlış userId ile boş döner")
    void shouldReturnEmptyForWrongUserId() {
        UUID userId = UUID.randomUUID();
        rateCardRepository.save(buildRateCard(userId));

        Optional<RateCard> result = rateCardRepository.findByUserId(UUID.randomUUID());

        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("findByUserId - kayıt yoksa boş döner")
    void shouldReturnEmptyWhenNoRateCard() {
        Optional<RateCard> result = rateCardRepository.findByUserId(UUID.randomUUID());

        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("save - tüm fiyat alanları doğru kaydedilir")
    void shouldSaveAllPriceFields() {
        UUID userId = UUID.randomUUID();
        RateCard saved = rateCardRepository.save(buildRateCard(userId));

        RateCard found = rateCardRepository.findById(saved.getId()).orElseThrow();

        assertThat(found.getWallM2Price()).isEqualTo(12.50);
        assertThat(found.getWallpaperM2Price()).isEqualTo(8.00);
        assertThat(found.getWindowDeductionM2()).isEqualTo(5.00);
        assertThat(found.getDoorDeductionM2()).isEqualTo(4.00);
        assertThat(found.getCeilingM2Price()).isEqualTo(10.00);
    }
}