package com.angebot.backend.dto;

import java.util.UUID;

public record SettingsResponse(
        String fistName,
        String lastName,
        String companyName,
        String phone,
        String email,
        String taxNumber,
        String street,
        String houseNr,
        String plz,
        String ort,
        UUID logoAssetId,
        UUID signatureAssetId
) {}