package com.angebot.backend.controller;

import com.angebot.backend.service.PdfReportService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:3002", allowCredentials = "true")
public class PdfController {

    private final PdfReportService pdfReportService;

    @GetMapping("/pdf/{offerId}")
    public ResponseEntity<byte[]> downloadPdf(@PathVariable Long offerId) {
        try {
            log.info("PDF-Anfrage erhalten, Offer ID: {}", offerId);

            byte[] pdf = pdfReportService.buildPdf(offerId);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("attachment", "angebot-" + offerId + ".pdf");
            headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");

            log.info("PDF erfolgreich erstellt, Größe: {} bytes", pdf.length);

            return new ResponseEntity<>(pdf, headers, HttpStatus.OK);

        } catch (IllegalArgumentException e) {
            log.error("PDF-Erstellungsfehler - keine Daten gefunden: {}", e.getMessage());
            return ResponseEntity.notFound().build();

        } catch (Exception e) {
            log.error("Fehler bei der PDF-Erstellung: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }
}