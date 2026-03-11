package com.angebot.backend.service;

import com.angebot.backend.dto.OfferEmailRequest;
import com.angebot.backend.entity.Customer;
import com.angebot.backend.entity.Offer;
import com.angebot.backend.entity.Settings;
import com.angebot.backend.entity.User;
import com.angebot.backend.repository.CustomerRepository;
import com.angebot.backend.repository.OfferRepository;
import com.angebot.backend.repository.SettingsRepository;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.javamail.JavaMailSender;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MailServiceTest {

    @Mock private JavaMailSender mailSender;
    @Mock private PdfReportService pdfReportService;
    @Mock private CustomerRepository customerRepository;
    @Mock private OfferRepository offerRepository;
    @Mock private SettingsRepository settingsRepository;

    @InjectMocks private MailService mailService;

    private UUID userId;
    private UUID customerId;
    private Offer offer;
    private Customer customer;
    private Settings settings;
    private MimeMessage mimeMessage;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        customerId = UUID.randomUUID();

        offer = new Offer();
        offer.setUserId(userId);
        offer.setCustomerId(customerId);
        offer.setWallM2(50.0);
        offer.setGrandTotal(500.0);

        User user = new User();
        user.setEmail("user@test.de");

        customer = new Customer();
        customer.setFirstName("Max");
        customer.setLastName("Mustermann");
        customer.setEmail("max@test.de");

        settings = new Settings();
        settings.setUser(user);
        settings.setFirstName("Hans");
        settings.setLastName("Maler");
        settings.setCompanyName("Maler GmbH");
        settings.setEmail("hans@maler.de");

        mimeMessage = mock(MimeMessage.class);
    }

    @Test
    @DisplayName("sendOfferPdf - email req.email ile mail gönderilir")
    void shouldSendMailWithRequestEmail() throws Exception {
        OfferEmailRequest req = new OfferEmailRequest(1L, "custom@test.de");

        when(pdfReportService.buildPdf(1L)).thenReturn(new byte[]{1, 2, 3});
        when(offerRepository.findById(1L)).thenReturn(Optional.of(offer));
        when(customerRepository.findById(customerId)).thenReturn(Optional.of(customer));
        when(settingsRepository.findByUserId(userId)).thenReturn(Optional.of(settings));
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);

        mailService.sendOfferPdf(req);

        verify(mailSender).send(mimeMessage);
    }

    @Test
    @DisplayName("sendOfferPdf - req.email null ise customer emaili kullanılır")
    void shouldSendMailWithCustomerEmail() throws Exception {
        OfferEmailRequest req = new OfferEmailRequest(1L, null);

        when(pdfReportService.buildPdf(1L)).thenReturn(new byte[]{1, 2, 3});
        when(offerRepository.findById(1L)).thenReturn(Optional.of(offer));
        when(customerRepository.findById(customerId)).thenReturn(Optional.of(customer));
        when(settingsRepository.findByUserId(userId)).thenReturn(Optional.of(settings));
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);

        mailService.sendOfferPdf(req);

        verify(mailSender).send(mimeMessage);
    }

    @Test
    @DisplayName("sendOfferPdf - offer bulunamazsa exception fırlatır")
    void shouldThrowIfOfferNotFound() throws Exception {
        OfferEmailRequest req = new OfferEmailRequest(99L, null);

        when(pdfReportService.buildPdf(99L)).thenReturn(new byte[]{1, 2, 3});
        when(offerRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> mailService.sendOfferPdf(req))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Offer not found");
    }

    @Test
    @DisplayName("sendOfferPdf - customer bulunamazsa exception fırlatır")
    void shouldThrowIfCustomerNotFound() throws Exception {
        OfferEmailRequest req = new OfferEmailRequest(1L, null);

        when(pdfReportService.buildPdf(1L)).thenReturn(new byte[]{1, 2, 3});
        when(offerRepository.findById(1L)).thenReturn(Optional.of(offer));
        when(customerRepository.findById(customerId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> mailService.sendOfferPdf(req))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Customer not found");
    }

    @Test
    @DisplayName("sendOfferPdf - settings bulunamazsa exception fırlatır")
    void shouldThrowIfSettingsNotFound() throws Exception {
        OfferEmailRequest req = new OfferEmailRequest(1L, null);

        when(pdfReportService.buildPdf(1L)).thenReturn(new byte[]{1, 2, 3});
        when(offerRepository.findById(1L)).thenReturn(Optional.of(offer));
        when(customerRepository.findById(customerId)).thenReturn(Optional.of(customer));
        when(settingsRepository.findByUserId(userId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> mailService.sendOfferPdf(req))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Settings not found");
    }


}