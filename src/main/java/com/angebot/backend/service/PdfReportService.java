package com.angebot.backend.service;

import com.angebot.backend.entity.*;
import com.angebot.backend.repository.AssetRepository;
import com.angebot.backend.repository.CustomerRepository;
import com.angebot.backend.repository.OfferRepository;
import com.angebot.backend.repository.RateCardRepository;
import com.angebot.backend.repository.SettingsRepository;
import com.lowagie.text.*;
import com.lowagie.text.Font;
import com.lowagie.text.Image;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class PdfReportService {

    private final SettingsRepository settingsRepo;
    private final CustomerRepository customerRepo;
    private final OfferRepository offerRepo;
    private final RateCardRepository rateCardRepo;
    private final AssetRepository assetRepo;

    public byte[] buildPdf(Long offerId) throws IOException {
        log.info("Erstellen einer PDF-Datei, Offer ID: {}", offerId);

        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            // ✅ ALLE DATEN PULLEN
            Offer offer = offerRepo.findById(offerId)
                    .orElseThrow(() -> new IllegalArgumentException("Angebot nicht gefunden: " + offerId));

            Customer customer = customerRepo.findById(offer.getCustomerId())
                    .orElseThrow(() -> new IllegalArgumentException("Kunde nicht gefunden"));

            Settings settings = settingsRepo.findByUserId(offer.getUserId())
                    .orElseThrow(() -> new IllegalArgumentException("Einstellungen nicht gefunden"));

            Document doc = new Document(PageSize.A4, 36, 36, 36, 36);
            PdfWriter.getInstance(doc, baos);
            doc.open();

            //  HEADER: Logo + Unternehmensinformationen
            addHeader(doc, settings);

            //  Lücke
            doc.add(new Paragraph("\n\n"));

            //  Informationen für Kunden
            addCustomerInfo(doc, customer);
            //  Lücke
            doc.add(new Paragraph("\n\n"));


            //  Angebot Header
            addOfferHeader(doc, offer, customer);
            //  Lücke
            doc.add(new Paragraph("\n\n"));
            // LEISTUNGEN Tablosu
            addItemsTable(doc, offer);

            //  Summary + Vergiftungen
            addSummaryTable(doc, offer);

            //  Footer
            addFooter(doc);
            doc.add(Chunk.NEWLINE);
            doc.add(Chunk.NEWLINE);
            //Name
            String userName= settings.getFirstName()+" "+settings.getLastName();
            Paragraph name=new Paragraph(userName,new Font(Font.HELVETICA, 11, Font.NORMAL));
            name.setAlignment(Element.ALIGN_LEFT);
            doc.add(name);
            doc.add(Chunk.NEWLINE);
            //Unterschreiben
            try{
                Image unterschreiben = loadImage(settings.getSignatureAssetId());
                if(unterschreiben!=null){
                    unterschreiben.scaleToFit(120, 60);
                    unterschreiben.setAlignment(Element.ALIGN_LEFT);
                    doc.add(unterschreiben);
                }
            }catch (Exception e){
                log.warn("Es gibt keine Unterschreiben");
            }

            //Close doc
            doc.close();

            byte[] pdfBytes = baos.toByteArray();
            log.info("PDF erstellt, Größe: {} bytes", pdfBytes.length);

            return pdfBytes;

        } catch (Exception e) {
            log.error("Fehler bei der PDF-Erstellung: {}", e.getMessage(), e);
            throw new IOException("PDF-Erstellung fehlgeschlagen: " + e.getMessage(), e);
        }
    }

    private void addHeader(Document doc, Settings settings) throws IOException {
        // 2-spaltige Tabelle: Links (Logo) und rechts (Unternehmensinformationen)
        PdfPTable table = new PdfPTable(2);
        table.setWidthPercentage(100);
        // Anpassen des Breitenverhältnisses (Links 40%, Rechts 60%, usw.)
        table.setWidths(new float[]{1f, 1.5f});

        // --- LOGO-ZELLE (LINKS)---
        PdfPCell logoCell = new PdfPCell();
        logoCell.setBorder(Rectangle.NO_BORDER);
        logoCell.setVerticalAlignment(Element.ALIGN_TOP); // Oben ausrichten

        try {
            if (settings.getLogoAssetId() != null) {
                Image logo = loadImage(settings.getLogoAssetId());
                logo.scaleToFit(120, 60);
                logoCell.addElement(logo);
            } else {
                logoCell.addElement(new Phrase("LOGO", new Font(Font.HELVETICA, 12, Font.BOLD)));
            }
        } catch (Exception e) {
            logoCell.addElement(new Phrase("LOGO", new Font(Font.HELVETICA, 12, Font.BOLD)));
        }
        table.addCell(logoCell);

        // --- INFORMATIONSZELLE DES UNTERNEHMENS (RECHTS) ---
        PdfPCell infoCell = new PdfPCell();
        infoCell.setBorder(Rectangle.NO_BORDER);

        // Die Zelle selbst rechtsbündig ausrichten
        infoCell.setHorizontalAlignment(Element.ALIGN_RIGHT);

        // Name des Unternehmens
        Paragraph companyName = new Paragraph(
                settings.getCompanyName() != null ? settings.getCompanyName() : "",
                new Font(Font.HELVETICA, 16, Font.BOLD));
        companyName.setAlignment(Element.ALIGN_RIGHT); // Yazıyı sağa yasla
        infoCell.addElement(companyName);

        // Adresse und Kontaktangaben
        String addressStr = String.format("%s %s\n%s %s\nTel: %s | %s",
                safe(settings.getStreet()),
                safe(settings.getHouseNr()),
                safe(settings.getPlz()),
                safe(settings.getOrt()),
                safe(settings.getPhone()),
                settings.getUser() != null ? safe(settings.getUser().getEmail()) : "");

        Paragraph addressPara = new Paragraph(addressStr, new Font(Font.HELVETICA, 11, Font.NORMAL));
        addressPara.setAlignment(Element.ALIGN_RIGHT); // Text rechtsbündig ausrichten
        addressPara.setLeading(14f);
        infoCell.addElement(addressPara);

        table.addCell(infoCell);
        doc.add(Chunk.NEWLINE);
        doc.add(table);
    }
    private void addCustomerInfo(Document doc, Customer customer) {
        Paragraph p = new Paragraph(
                customer.getFirstName() + " " + customer.getLastName() + "\n" +
                        safe(customer.getHomeStreet()) + " " + safe(customer.getHomeHouseNr()) + "\n" +
                        safe(customer.getHomePlz()) + " " + safe(customer.getHomeOrt()) + "\n" +
                        "Tel: " + safe(customer.getPhone()) + " | " + safe(customer.getEmail()),
                new Font(Font.HELVETICA, 12, Font.BOLD)
        );
        p.setSpacingAfter(10f);
        doc.add(p);
    }

    private void addOfferHeader(Document doc, Offer offer, Customer customer) {
        PdfPTable table = new PdfPTable(4);
        table.setWidthPercentage(100);
        table.setWidths(new float[]{2, 2, 2, 2});

        LocalDate datum= LocalDate.ofInstant(offer.getCreatedAt(), ZoneId.of("Europe/Berlin"));
        LocalDate gultigDatum= datum.plusDays(30L);
        addInfoCell(table, "Angebots-Nr.:", String.valueOf(offer.getId()));
        addInfoCell(table, "Gültig bis:", gultigDatum.format(DateTimeFormatter.ofPattern("dd.MM.yyyy")));
        addInfoCell(table, "Datum:",
                datum.format(DateTimeFormatter.ofPattern("dd.MM.yyyy")));
        addInfoCell(table, "Kunden-Nr.:",
                customer.getId().toString().substring(0, 8));

        doc.add(table);
    }

    private void addItemsTable(Document doc, Offer offer) {
        PdfPTable table = new PdfPTable(6);
        table.setWidthPercentage(100);
        table.setWidths(new float[]{0.8f, 1.2f, 3.5f, 1f, 1f, 1.5f});

        // Header
        String[] headers = {"Pos.", "Art-Nr.", "Leistung", "Menge", "Einheit", "Gesamt (€)"};
        for (String h : headers) {
            addHeaderCell(table, h);
        }

        // 1. Wand
        if (offer.getWallM2() > 0) {
            table.addCell(createCell("1", true));
            table.addCell(createCell("W-001", false));
            table.addCell(createCell("Wand streichen", false));
            table.addCell(createCell(String.format("%.1f", offer.getWallM2()), false));
            table.addCell(createCell("m²", false));
            table.addCell(createCell(String.format("€ %.2f", offer.getWallTotal()), false));
        }

        // 2. Tapeta
        if (offer.getWallpaperM2() > 0) {
            table.addCell(createCell("2", true));
            table.addCell(createCell("T-001", false));
            table.addCell(createCell("Tapeten verlegen", false));
            table.addCell(createCell(String.format("%.1f", offer.getWallpaperM2()), false));
            table.addCell(createCell("m²", false));
            table.addCell(createCell(String.format("€ %.2f", offer.getWallpaperTotal()), false));
        }

        // 3. Decke
        if (offer.getCeilingM2() > 0) {
            table.addCell(createCell("3", true));
            table.addCell(createCell("D-001", false));
            table.addCell(createCell("Decke verputzen", false));
            table.addCell(createCell(String.format("%.1f", offer.getCeilingM2()), false));
            table.addCell(createCell("m²", false));
            table.addCell(createCell(String.format("€ %.2f", offer.getCeilingTotal()), false));
        }

        doc.add(table);
    }

    private void addSummaryTable(Document doc, Offer offer) {
        doc.add(new Paragraph("\n"));

        PdfPTable table = new PdfPTable(2);
        table.setWidthPercentage(60);
        table.setHorizontalAlignment(Element.ALIGN_RIGHT);
        table.setWidths(new float[]{2, 1});

        double netto = offer.getGrandTotal();
        double ust = netto * 0.19;
        double brutto = netto * 1.19;

        addSummaryCell(table, "Summe Netto:", String.format("€ %.2f", netto));
        addSummaryCell(table, "19% USt.:", String.format("€ %.2f", ust));

        // Endsumme (BOLD)
        PdfPCell endLabel = createCell("Endsumme:", true);
        endLabel.setColspan(1);
        table.addCell(endLabel);
        table.addCell(createCell(String.format("€ %.2f", brutto), true));

        doc.add(table);
    }

    private void addFooter(Document doc) {
        doc.add(new Paragraph("\n\n"));
        Paragraph footer = new Paragraph(
                "• Lieferung frei Haus\n" +
                        "• Zahlbar: 14 Tage 2% Skonto / 30 Tage netto\n" +
                        "• Angebot freibleibend • Preise netto zzgl. USt.",
                new Font(Font.HELVETICA, 9, Font.ITALIC)
        );
        footer.setAlignment(Element.ALIGN_LEFT);
        doc.add(footer);
    }

    private void addHeaderCell(PdfPTable table, String text) {
        PdfPCell cell = new PdfPCell(new Phrase(text,
                new Font(Font.HELVETICA, 10, Font.BOLD)));
        cell.setBackgroundColor(new Color(220, 220, 220));
        cell.setPadding(8f);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell(cell);
    }

    private PdfPCell createCell(String text, boolean bold) {
        Font font = bold ?
                new Font(Font.HELVETICA, 10, Font.BOLD) :
                new Font(Font.HELVETICA, 10, Font.NORMAL);
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setPadding(6f);
        cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        return cell;
    }

    private void addInfoCell(PdfPTable table, String label, String value) {
        table.addCell(createCell(label, true));
        table.addCell(createCell(value, false));
    }

    private void addSummaryCell(PdfPTable table, String label, String value) {
        table.addCell(createCell(label, false));
        table.addCell(createCell(value, true));
    }

    private Image loadImage(UUID assetId) throws IOException {
        if (assetId == null) {
            return null;
        }

        byte[] originalBytes = assetRepo.findById(assetId)
                .orElseThrow(() -> new IllegalArgumentException("Asset nicht gefunden: " + assetId))
                .getData();

        // 1️⃣ Byte → BufferedImage
        java.awt.image.BufferedImage bufferedImage =
                javax.imageio.ImageIO.read(new java.io.ByteArrayInputStream(originalBytes));

        if (bufferedImage == null) {
            log.warn("Logo konnte nicht decodiert werden");
            return null;
        }

        // 2️⃣ PNG olarak yeniden encode et
        java.io.ByteArrayOutputStream baos = new java.io.ByteArrayOutputStream();
        javax.imageio.ImageIO.write(bufferedImage, "png", baos);
        byte[] safePng = baos.toByteArray();

        // 3️⃣ PDF Image oluştur
        return Image.getInstance(safePng);
    }

    private String safe(Object value) {
        return value != null ? value.toString() : "";
    }
}