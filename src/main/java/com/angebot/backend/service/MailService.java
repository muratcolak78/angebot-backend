package com.angebot.backend.service;

import com.angebot.backend.dto.OfferEmailRequest;
import com.angebot.backend.entity.Customer;
import com.angebot.backend.entity.Offer;
import com.angebot.backend.entity.Settings;
import com.angebot.backend.repository.CustomerRepository;
import com.angebot.backend.repository.OfferRepository;
import com.angebot.backend.repository.SettingsRepository;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
@RequiredArgsConstructor
public class MailService {

    private final JavaMailSender mailSender;
    private final PdfReportService pdfReportService;
    private final CustomerRepository customerRepository;
    private final OfferRepository offerRepository;
    private final SettingsRepository settingsRepository;

    public void sendOfferPdf(OfferEmailRequest req) throws MessagingException, IOException {
        // 1) PDF erzeugen
        byte[] pdfBytes = pdfReportService.buildPdf(req.offerId());

        // 2) Offer laden (sauber, ohne Lazy-Probleme)
        Offer offer = offerRepository.findById(req.offerId())
                .orElseThrow(() -> new IllegalStateException("Offer not found: " + req.offerId()));

        // 3) Customer laden
        Customer customer = customerRepository.findById(offer.getCustomerId())
                .orElseThrow(() -> new IllegalStateException("Customer not found: " + offer.getCustomerId()));

        // 4) Settings / Userdaten laden
        Settings userSettings = settingsRepository.findByUserId(offer.getUserId())
                .orElseThrow(() -> new IllegalStateException("Settings not found for userId: " + offer.getUserId()));

        // 5) Zieladresse bestimmen: req.email (optional) sonst customer.email
        String targetEmail = resolveTargetEmail(req, customer);

        String subject = "Angebot #" + req.offerId();

        StringBuilder text = new StringBuilder();
        text.append("Sehr geehrte/r ").append(customer.getLastName()).append("\n\n");
        text.append("anbei finden Sie unser Angebot.\n\n");
        text.append("Mit freundlichen Grüßen\n\n");
        text.append(userSettings.getFirstName()).append(" ").append(userSettings.getLastName());

        MimeMessage mail = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mail, true, "UTF-8");

        // Achtung: "From" muss oft eine echte Domain/Adresse sein, die dein SMTP erlaubt.
        helper.setFrom("test.colak@gmail.com", userSettings.getCompanyName());

        helper.setTo(targetEmail);
        helper.setSubject(subject);
        helper.setText(text.toString(), false);

        // Reply-To: Antworten gehen an den Benutzer
        if (userSettings.getEmail() != null && !userSettings.getEmail().isBlank()) {
            helper.setReplyTo(userSettings.getEmail());
        }

        helper.addAttachment(
                "angebot-" + req.offerId() + ".pdf",
                new ByteArrayResource(pdfBytes)
        );

        mailSender.send(mail);
    }
    private String resolveTargetEmail(OfferEmailRequest req, Customer customer) {
        // req.email kann null oder blank sein
        if (req.email() != null && !req.email().isBlank()) {
            return req.email().trim();
        }

        if (customer.getEmail() == null || customer.getEmail().isBlank()) {
            throw new IllegalStateException("Customer has no email address (customerId: " + customer.getId() + ")");
        }

        return customer.getEmail().trim();
    }
}