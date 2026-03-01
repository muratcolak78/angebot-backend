package com.angebot.backend.dto;

import lombok.Data;
import java.util.UUID;

@Data
public class RateCardDTO {
    private UUID id;
    private UUID userId;
    
    // Pricing (€/m²)
    private double wallM2Price;
    private double wallpaperM2Price;
    private double windowDeductionM2;
    private double doorDeductionM2;
    private double ceilingM2Price;
    
    private String createdAt;
    private String updatedAt;
}
