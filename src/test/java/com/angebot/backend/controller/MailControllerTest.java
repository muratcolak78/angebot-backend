package com.angebot.backend.controller;

import com.angebot.backend.dto.OfferEmailRequest;
import com.angebot.backend.service.MailService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class MailControllerTest {

    @Mock
    private MailService mailService;

    @InjectMocks
    private MailController controller;

    @Test
    @DisplayName("POST /api/mail/offer ")
    void shouldSendOfferMail() throws Exception {
        OfferEmailRequest req = new OfferEmailRequest(1233L,"test@mail.com");

        doNothing().when(mailService).sendOfferPdf(any());

        ResponseEntity<?> result = controller.sendOffer(req);

        assertThat(result.getStatusCode().value()).isEqualTo(200);
        verify(mailService).sendOfferPdf(req);
    }
}