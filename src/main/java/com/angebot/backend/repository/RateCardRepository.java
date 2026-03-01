package com.angebot.backend.repository;

import com.angebot.backend.entity.RateCard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface RateCardRepository extends JpaRepository<RateCard, UUID> {
    Optional<RateCard> findByUserId(UUID userId);
    Optional<RateCard> findLatestByUserId(UUID userId);
    boolean existsByUserId(UUID userId);
}
