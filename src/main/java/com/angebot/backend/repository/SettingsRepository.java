package com.angebot.backend.repository;

import com.angebot.backend.entity.Settings;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.UUID;

public interface SettingsRepository extends JpaRepository<Settings, UUID> {
    Optional<Settings> findByUserEmail(String email);
    boolean existsByUserEmail(String email);

    Optional<Settings> findByUserId(UUID userId);
}