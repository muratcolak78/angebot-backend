package com.angebot.backend.controller;

import com.angebot.backend.dto.OfferEmailRequest;
import com.angebot.backend.service.MailService;
import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/api/mail")
@RequiredArgsConstructor
public class MailController {

    private final MailService mailService;

    @PostMapping("/offer")
    public ResponseEntity<?> sendOffer(@Valid @RequestBody OfferEmailRequest req) throws MessagingException, IOException {
        mailService.sendOfferPdf(req);
        return ResponseEntity.ok().build();
    }
}