package com.angebot.backend.dto;

import lombok.Data;


public record CustomerUpdateDTO (
    String firstName,
    String lastName,
    String email,
    String phone,
    String homeStreet,
    String homeHouseNr,
    String homePlz,
    String homeOrt,
    String workStreet,
    String workHouseNr,
    String workPlz,
    String workOrt
){}
