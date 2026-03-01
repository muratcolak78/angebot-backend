package com.angebot.backend.dto;

import lombok.Data;

@Data
public class RateCardUpdateDTO {  // ← userId YOK!
    private Double wallM2Price;
    private Double wallpaperM2Price;
    private Double windowDeductionM2;
    private Double doorDeductionM2;
    private Double ceilingM2Price;
}
