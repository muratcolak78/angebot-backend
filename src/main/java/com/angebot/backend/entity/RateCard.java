package com.angebot.backend.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "rate_cards", indexes = {@Index(columnList = "user_id")})
@Getter @Setter
public class RateCard {
    @Id @GeneratedValue private UUID id;

    @Column(nullable = false, name = "user_id")
    private UUID userId;

    // ✅ DB + Kod double default
    @Column(nullable = false, name = "wall_m2_price",
            columnDefinition = "numeric(10,2)")
    private double wallM2Price;

    @Column(nullable = false, name = "wallpaper_m2_price",
            columnDefinition = "numeric(10,2)")
    private double wallpaperM2Price;

    @Column(name = "window_deduction_m2",
            columnDefinition = "numeric(10,2)")
    private double windowDeductionM2;

    @Column(name = "door_deduction_m2",
            columnDefinition = "numeric(10,2)")
    private double doorDeductionM2;

    @Column(name = "ceiling_m2_price",
            columnDefinition = "numeric(10,2)")
    private double ceilingM2Price ;

    @Column(nullable = false, name = "created_at")
    private Instant createdAt = Instant.now();

    @Column(name = "updated_at")
    private Instant updatedAt;
}
