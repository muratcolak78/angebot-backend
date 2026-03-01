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
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MailService {

    private final JavaMailSender mailSender;
    private final PdfReportService pdfReportService;
    private final CustomerRepository customerRepository;
    private  final OfferRepository offerRepository;
    private  final SettingsRepository settingsRepository;


    public void sendOfferPdf(OfferEmailRequest req) throws MessagingException, IOException {
        byte[] pdfBytes = pdfReportService.buildPdf(req.offerId());
        Offer offer=offerRepository.getReferenceById(req.offerId());

        Customer customer = customerRepository.findById(offer.getCustomerId())
                .orElseThrow(() -> new IllegalStateException("Customer not found: " + offer.getCustomerId()));
        System.out.println("-------------------- Customer email: "+customer.getEmail());
        Optional<Settings> userOptional=settingsRepository.findByUserId(offer.getUserId());
        Settings user= userOptional.get();
        String userEmail= user.getEmail();
        System.out.println("-------------------- User email: "+userEmail);
        System.out.println("-------------------- User firstname and last name: "+user.getFirstName()+" "+user.getLastName());
        String customerEmail= customer.getEmail();
        System.out.println(user);

        String subject = "Angebot #" + req.offerId();

        StringBuilder text=new StringBuilder();
        text.append("Sehr geehrte/r "+customer.getLastName()+"\n\n");
        text.append("Anbei finden Sie unser Angebot"+"\n");
        text.append("Mit freundlichen Grüßen"+"\n\n");
        text.append(user.getFirstName()+" "+user.getLastName());


        MimeMessage mail = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mail, true, "UTF-8");
        helper.setFrom("no-repliy@angebot.de", user.getCompanyName());
        helper.setTo(customerEmail);
        helper.setSubject(subject);
        helper.setText(text.toString(), false);
        System.out.println("EMAIL :"+user.getEmail());
        helper.setReplyTo(user.getEmail());
        helper.addAttachment("angebot-" + req.offerId() + ".pdf",
                new ByteArrayResource(pdfBytes));

        mailSender.send(mail);
    }
}