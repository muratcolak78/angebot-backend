package com.angebot.backend.repository;

import com.angebot.backend.entity.Offer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface OfferRepository extends JpaRepository<Offer, Long> {
    List<Offer> findByUserId(UUID userId);
    Optional<Offer> findByUserIdAndId(UUID userId, Long id);
    List<Offer> findByUserIdOrderByCreatedAtDesc(UUID userId);
    boolean existsByCustomerId(UUID id);
    void deleteByCustomerId(UUID customerId);
}
