package com.angebot.backend.repository;

import com.angebot.backend.entity.Offer;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class OfferRepositoryTest {

    @Autowired
    private OfferRepository offerRepository;

    private Offer buildOffer(UUID userId, UUID customerId) {
        Offer o = new Offer();
        o.setUserId(userId);
        o.setCustomerId(customerId);
        o.setWallM2(50.0);
        o.setWallTotal(500.0);
        o.setGrandTotal(500.0);
        return o;
    }

    @Test
    @DisplayName("findByUserId - kullanıcıya ait tüm teklifleri döner")
    void shouldFindAllOffersByUserId() {
        UUID userId = UUID.randomUUID();

        offerRepository.save(buildOffer(userId, UUID.randomUUID()));
        offerRepository.save(buildOffer(userId, UUID.randomUUID()));
        offerRepository.save(buildOffer(UUID.randomUUID(), UUID.randomUUID()));

        List<Offer> result = offerRepository.findByUserId(userId);

        assertThat(result).hasSize(2);
        assertThat(result).allMatch(o -> o.getUserId().equals(userId));
    }

    @Test
    @DisplayName("findByUserIdAndId - doğru teklifi döner")
    void shouldFindOfferByUserIdAndId() {
        UUID userId = UUID.randomUUID();
        Offer saved = offerRepository.save(buildOffer(userId, UUID.randomUUID()));

        Optional<Offer> result = offerRepository.findByUserIdAndId(userId, saved.getId());

        assertThat(result).isPresent();
        assertThat(result.get().getWallM2()).isEqualTo(50.0);
    }

    @Test
    @DisplayName("findByUserIdAndId - yanlış userId ile bulunamaz")
    void shouldNotFindOfferWithWrongUserId() {
        UUID userId = UUID.randomUUID();
        Offer saved = offerRepository.save(buildOffer(userId, UUID.randomUUID()));

        Optional<Offer> result = offerRepository.findByUserIdAndId(UUID.randomUUID(), saved.getId());

        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("findByUserIdOrderByCreatedAtDesc - en yeni teklif başta gelir")
    void shouldReturnOffersOrderedByCreatedAtDesc() throws InterruptedException {
        UUID userId = UUID.randomUUID();

        Offer first = offerRepository.save(buildOffer(userId, UUID.randomUUID()));
        Thread.sleep(50);
        Offer second = offerRepository.save(buildOffer(userId, UUID.randomUUID()));

        List<Offer> result = offerRepository.findByUserIdOrderByCreatedAtDesc(userId);

        assertThat(result).hasSize(2);
        assertThat(result.get(0).getId()).isEqualTo(second.getId());
        assertThat(result.get(1).getId()).isEqualTo(first.getId());
    }

    @Test
    @DisplayName("existsByCustomerId - müşteriye ait teklif varsa true döner")
    void shouldReturnTrueIfOfferExistsForCustomer() {
        UUID customerId = UUID.randomUUID();
        offerRepository.save(buildOffer(UUID.randomUUID(), customerId));

        boolean exists = offerRepository.existsByCustomerId(customerId);

        assertThat(exists).isTrue();
    }

    @Test
    @DisplayName("existsByCustomerId - teklif yoksa false döner")
    void shouldReturnFalseIfNoOfferForCustomer() {
        boolean exists = offerRepository.existsByCustomerId(UUID.randomUUID());

        assertThat(exists).isFalse();
    }

    @Test
    @DisplayName("deleteByCustomerId - müşteriye ait teklifler silinir")
    void shouldDeleteOffersByCustomerId() {
        UUID customerId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();

        offerRepository.save(buildOffer(userId, customerId));
        offerRepository.save(buildOffer(userId, customerId));
        offerRepository.save(buildOffer(userId, UUID.randomUUID()));

        offerRepository.deleteByCustomerId(customerId);

        List<Offer> remaining = offerRepository.findByUserId(userId);
        assertThat(remaining).hasSize(1);
        assertThat(remaining.get(0).getCustomerId()).isNotEqualTo(customerId);
    }
}