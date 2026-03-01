package com.angebot.backend.controller;


import com.angebot.backend.service.SettingsService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/assets")
public class AssetController {

    private final SettingsService service;

    public AssetController(SettingsService service) {
        this.service = service;
    }

    @GetMapping("/{id}")
    public ResponseEntity<byte[]> get(@PathVariable UUID id) {
        var a = service.getAsset(id);
        return ResponseEntity.ok()
                .header("Content-Type", a.getContentType())
                .body(a.getData());
    }
}