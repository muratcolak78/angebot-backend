package com.angebot.backend.controller;

import com.angebot.backend.dto.RateCardCreateDTO;
import com.angebot.backend.dto.RateCardDTO;
import com.angebot.backend.dto.RateCardUpdateDTO;
import com.angebot.backend.service.RateCardService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/ratecard")
@RequiredArgsConstructor
public class RateCardController {

    private final RateCardService service;

    // GET - Rückgabe, wenn RateCard vorhanden ist, sonst 404
    @GetMapping("/me")
    public ResponseEntity<RateCardDTO> getMyRateCard() {
        RateCardDTO dto = service.getMyRateCard();
        return ResponseEntity.ok(dto);
    }

    // POST - Neue RateCard erstellen
    @PostMapping("/me")
    public ResponseEntity<RateCardDTO> createMyRateCard(@Valid @RequestBody RateCardCreateDTO createDTO) {
        RateCardDTO dto = service.createMyRateCard(createDTO);
        return new ResponseEntity<>(dto, HttpStatus.CREATED);
    }

    // PUT - Vorhandene RateCard aktualisieren
    @PutMapping("/me")
    public ResponseEntity<RateCardDTO> updateMyRateCard(@Valid @RequestBody RateCardUpdateDTO dto) {
        RateCardDTO updated = service.updateMyRateCard(dto);
        return ResponseEntity.ok(updated);
    }

    // DELETE - RateCard löschen (optional)
    @DeleteMapping("/me")
    public ResponseEntity<Void> deleteMyRateCard() {
        service.deleteMyRateCard();
        return ResponseEntity.noContent().build();
    }
}