package com.angebot.backend.dto;

import lombok.Data;

import java.util.UUID;

@Data
public class CustomerDTO {
    private UUID id;
    private String firstName,
            lastName,
            email,
            phone,
            homeStreet,
            homeHouseNr,
            homePlz,
            homeOrt,
            workStreet,
            workHouseNr,
            workPlz,
            workOrt;
}


