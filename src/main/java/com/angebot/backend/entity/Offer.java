package com.angebot.backend.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "offers")
@Getter
@Setter
public class Offer {
    @Id
    @GeneratedValue
    private Long id;
    
    @Column(nullable = false, name = "user_id") private UUID userId;
    @Column(nullable = false, name = "customer_id") private UUID customerId;
    
    // Inputs
    @Column(nullable = false, name = "wall_m2") private double wallM2;
    @Column(name = "wallpaper_m2") private double wallpaperM2 = 0;
    @Column(name = "ceiling_m2") private double ceilingM2 = 0;
    @Column(name = "windows_m2") private double windowsM2 = 0;
    private int doors = 0;
    
    // Outputs
    @Column(name = "wall_total") private double wallTotal;
    @Column(name = "wallpaper_total") private double wallpaperTotal = 0;
    @Column(name = "ceiling_total") private double ceilingTotal = 0;
    @Column(name = "window_deduction") private double windowDeduction = 0;
    @Column(name = "door_deduction") private double doorDeduction = 0;
    @Column(name = "grand_total") private double grandTotal;
    
    @Column(nullable = false, name = "created_at")
    private Instant createdAt = Instant.now();
    @Column(name = "updated_at")
    private Instant updatedAt;
}
