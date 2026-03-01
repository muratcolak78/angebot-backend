package com.angebot.backend.dto;

public record RateCardCreateDTO(
        Double wallM2Price,
        Double wallpaperM2Price,
        Double windowDeductionM2,
        Double doorDeductionM2,
        Double ceilingM2Price
) {
}
