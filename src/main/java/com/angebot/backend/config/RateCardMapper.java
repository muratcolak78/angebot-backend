package com.angebot.backend.config;

import com.angebot.backend.dto.RateCardDTO;
import com.angebot.backend.dto.RateCardUpdateDTO;
import com.angebot.backend.entity.RateCard;
import org.springframework.stereotype.Component;
@Component
public class RateCardMapper {

    // GET → Tam DTO
    public RateCardDTO toDTO(RateCard entity) {
        RateCardDTO dto = new RateCardDTO();
        dto.setId(entity.getId());
        dto.setUserId(entity.getUserId());
        dto.setWallM2Price(entity.getWallM2Price());
        dto.setWallpaperM2Price(entity.getWallpaperM2Price());
        dto.setWindowDeductionM2(entity.getWindowDeductionM2());
        dto.setDoorDeductionM2(entity.getDoorDeductionM2());
        dto.setCeilingM2Price(entity.getCeilingM2Price());
        dto.setCreatedAt(entity.getCreatedAt().toString());
        dto.setUpdatedAt(entity.getUpdatedAt() != null ?
                entity.getUpdatedAt().toString() : null);
        return dto;
    }

    // PUT → EXISTING entity UPDATE et!
    public void updateEntity(RateCardUpdateDTO dto, RateCard entity) {
        // Null-safe partial update
        if (dto.getWallM2Price() != null)
            entity.setWallM2Price(dto.getWallM2Price());
        if (dto.getWallpaperM2Price() != null)
            entity.setWallpaperM2Price(dto.getWallpaperM2Price());
        if (dto.getWindowDeductionM2() != null)
            entity.setWindowDeductionM2(dto.getWindowDeductionM2());
        if (dto.getDoorDeductionM2() != null)
            entity.setDoorDeductionM2(dto.getDoorDeductionM2());
        if (dto.getCeilingM2Price() != null)
            entity.setCeilingM2Price(dto.getCeilingM2Price());
    }
}
