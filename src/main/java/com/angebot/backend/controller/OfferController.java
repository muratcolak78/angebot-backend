package com.angebot.backend.controller;

import com.angebot.backend.dto.OfferCreateDTO;
import com.angebot.backend.dto.OfferDTO;
import com.angebot.backend.service.OfferService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/offers")
@RequiredArgsConstructor
public class OfferController {

    private final OfferService service;

    // Neues Angebot erstellen
    @PostMapping
    public ResponseEntity<OfferDTO> createOffer(@Valid @RequestBody OfferCreateDTO dto) {
        return ResponseEntity.ok(service.createOffer(dto));
    }

    // Meine Angebote
    @GetMapping("/me")
    public List<OfferDTO> getMyOffers() {
        return service.getMyOffers();
    }

    // Teklif detay
    @GetMapping("/{id}")
    public OfferDTO getOffer(@PathVariable Long id) {
        return service.getOffer(id);
    }

    // Angebot aktualisieren (Größe ändern)
    @PutMapping("/{id}")
    public OfferDTO updateOffer(@PathVariable Long id, @Valid @RequestBody OfferCreateDTO dto) {
        return service.updateOffer(id, dto);
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOffer(@PathVariable Long id) {
        service.deleteOffer(id);
        return ResponseEntity.noContent().build();
    }
}
