package com.angebot.backend.dto;

import jakarta.validation.constraints.NotBlank;

public record SettingsRequest(
        @NotBlank String firstName,
        String lastName,
        String companyName,
        String address,
        String phone,
        String email,
        String taxNumber,
        String street,
        String houseNr,
        String plz,
        String ort
) {}