package com.angebot.backend.dto;

public record CustomerCreateDTO (
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