package com.angebot.backend.dto;

import lombok.Data;

import java.util.UUID;

@Data
public class OfferCreateDTO {
    private UUID customerId;  // ← Dropdown'dan!
    
    // Measurements (m², adet)
    private double wallM2;
    private double wallpaperM2;
    private double ceilingM2;
    private double windowsM2;
    private int doors;

}
