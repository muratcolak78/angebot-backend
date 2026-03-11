package com.angebot.backend.controller;

import com.angebot.backend.service.PdfReportService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PdfControllerTest {

    @Mock
    private PdfReportService pdfReportService;

    @InjectMocks
    private PdfController controller;

    @Test
    @DisplayName("GET /api/pdf/{offerId} - PDF başarıyla döner")
    void shouldReturnPdf() throws Exception {
        byte[] pdfBytes = new byte[]{1, 2, 3, 4};
        when(pdfReportService.buildPdf(1L)).thenReturn(pdfBytes);

        ResponseEntity<byte[]> result = controller.downloadPdf(1L);

        assertThat(result.getStatusCode().value()).isEqualTo(200);
        assertThat(result.getBody()).isEqualTo(pdfBytes);
        assertThat(result.getHeaders().getContentType().toString()).contains("application/pdf");
    }

    @Test
    @DisplayName("GET /api/pdf/{offerId} - offer bulunamazsa 404 döner")
    void shouldReturn404IfOfferNotFound() throws Exception {
        when(pdfReportService.buildPdf(99L))
                .thenThrow(new IllegalArgumentException("NOT_FOUND"));

        ResponseEntity<byte[]> result = controller.downloadPdf(99L);

        assertThat(result.getStatusCode().value()).isEqualTo(404);
    }

    @Test
    @DisplayName("GET /api/pdf/{offerId} - beklenmedik hata 500 döner")
    void shouldReturn500OnUnexpectedError() throws Exception {
        when(pdfReportService.buildPdf(1L))
                .thenThrow(new RuntimeException("Unexpected"));

        ResponseEntity<byte[]> result = controller.downloadPdf(1L);

        assertThat(result.getStatusCode().value()).isEqualTo(500);
    }
}