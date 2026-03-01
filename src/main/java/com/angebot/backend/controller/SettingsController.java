package com.angebot.backend.controller;

import com.angebot.backend.dto.SettingsRequest;
import com.angebot.backend.dto.SettingsResponse;
import com.angebot.backend.service.SettingsService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/settings")
@CrossOrigin("*")
@RequiredArgsConstructor
public class SettingsController {

    private final SettingsService service;

    // Get maine Einstellungen
    @GetMapping("/me")
    public SettingsResponse getMe() {
        return service.getMySettings();
    }
    //post meine einstellungen
    @PutMapping("/me")
    public SettingsResponse updateMe(@Valid @RequestBody SettingsRequest req) {
        return service.updateMySettings(req);
    }

    //post mein logo
    @PostMapping("/me/logo")
    public Map<String, UUID> uploadLogo(@RequestParam("file") MultipartFile file) throws IOException {
        UUID id = service.uploadLogo(file);
        return Map.of("assetId", id);
    }
    //post meine Unsterschreiben
    @PostMapping("/me/signature")
    public Map<String, UUID> uploadSignature(@RequestParam("file") MultipartFile file) throws IOException {
        UUID id = service.uploadSignature(file);
        return Map.of("assetId", id);
    }
}