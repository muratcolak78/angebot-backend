package com.angebot.backend.dto;

import lombok.Data;

import java.time.Instant;
import java.util.UUID;

@Data
public class OfferDTO {
    private Long id;
    private UUID customerId, userId;
    //customer info
    private String customerFirstName;
    private String customerLastName;
    private String customerEmail;
    private String customerPhone;
    // Inputs
    private double wallM2, wallpaperM2, ceilingM2, windowsM2;
    private int doors;
    
    // Outputs (hesaplanmış)
    private double wallTotal, wallpaperTotal, ceilingTotal;
    private double windowDeduction, doorDeduction;
    private double grandTotal;
    private Instant createdAt;
    private Instant updatedAt;
}
