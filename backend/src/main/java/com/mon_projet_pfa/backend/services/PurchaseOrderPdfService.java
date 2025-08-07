package com.mon_projet_pfa.backend.services;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
public class PurchaseOrderPdfService {

    public byte[] generatePurchaseOrderPdf(String supplierName, String productName,
            Integer quantity, Double totalAmount,
            String status) throws IOException {
        try (PDDocument document = new PDDocument()) {
            PDPage page = new PDPage(PDRectangle.A4);
            document.addPage(page);

            try (PDPageContentStream contentStream = new PDPageContentStream(document, page)) {
                // Utilisation des nouvelles constantes de PDFBox 3.x
                PDType1Font fontBold = new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD);
                PDType1Font fontRegular = new PDType1Font(Standard14Fonts.FontName.HELVETICA);

                float margin = 50;
                float yPosition = page.getMediaBox().getHeight() - margin;
                float fontSize = 12;
                float titleFontSize = 16;

                // Titre
                contentStream.beginText();
                contentStream.setFont(fontBold, titleFontSize);
                contentStream.newLineAtOffset(margin, yPosition);
                contentStream.showText("BON DE COMMANDE - " + status);
                contentStream.endText();

                yPosition -= 40;

                // Informations de base
                contentStream.beginText();
                contentStream.setFont(fontRegular, fontSize);
                contentStream.newLineAtOffset(margin, yPosition);
                contentStream.showText("Fournisseur: " + supplierName);
                contentStream.endText();

                yPosition -= 20;

                contentStream.beginText();
                contentStream.setFont(fontRegular, fontSize);
                contentStream.newLineAtOffset(margin, yPosition);
                contentStream.showText(
                        "Date: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));
                contentStream.endText();

                yPosition -= 40;

                // En-têtes du tableau
                contentStream.beginText();
                contentStream.setFont(fontBold, fontSize);
                contentStream.newLineAtOffset(margin, yPosition);
                contentStream.showText("Produit");
                contentStream.newLineAtOffset(150, 0);
                contentStream.showText("Quantité");
                contentStream.newLineAtOffset(100, 0);
                contentStream.showText("Prix total");
                contentStream.endText();

                yPosition -= 30;

                // Ligne de séparation
                contentStream.moveTo(margin, yPosition);
                contentStream.lineTo(page.getMediaBox().getWidth() - margin, yPosition);
                contentStream.stroke();

                yPosition -= 20;

                // Détails du produit
                contentStream.beginText();
                contentStream.setFont(fontRegular, fontSize);
                contentStream.newLineAtOffset(margin, yPosition);
                contentStream.showText(productName);
                contentStream.newLineAtOffset(150, 0);
                contentStream.showText(quantity.toString());
                contentStream.newLineAtOffset(100, 0);
                contentStream.showText(String.format("%.2f €", totalAmount));
                contentStream.endText();

                yPosition -= 40;

                // Total
                contentStream.beginText();
                contentStream.setFont(fontBold, fontSize);
                contentStream.newLineAtOffset(page.getMediaBox().getWidth() - margin - 100, yPosition);
                contentStream.showText("TOTAL: " + String.format("%.2f €", totalAmount));
                contentStream.endText();
            }

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            document.save(baos);
            return baos.toByteArray();
        }
    }

    // Méthode alternative avec des paramètres personnalisés
    public byte[] generateCustomPurchaseOrderPdf(String orderNumber, String customerName,
            String productName, int quantity,
            double unitPrice) throws IOException {
        try (PDDocument document = new PDDocument()) {
            PDPage page = new PDPage(PDRectangle.A4);
            document.addPage(page);

            try (PDPageContentStream contentStream = new PDPageContentStream(document, page)) {
                // Polices
                PDType1Font fontBold = new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD);
                PDType1Font fontRegular = new PDType1Font(Standard14Fonts.FontName.HELVETICA);

                float margin = 50;
                float yPosition = page.getMediaBox().getHeight() - margin;
                float fontSize = 12;
                float titleFontSize = 18;

                // Titre principal
                contentStream.beginText();
                contentStream.setFont(fontBold, titleFontSize);
                contentStream.newLineAtOffset(margin, yPosition);
                contentStream.showText("BON DE COMMANDE");
                contentStream.endText();

                yPosition -= 50;

                // Informations de commande
                addTextLine(contentStream, fontBold, fontSize, margin, yPosition, "Numéro de commande: " + orderNumber);
                yPosition -= 20;

                addTextLine(contentStream, fontRegular, fontSize, margin, yPosition, "Date: " +
                        LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
                yPosition -= 20;

                addTextLine(contentStream, fontRegular, fontSize, margin, yPosition, "Client: " + customerName);
                yPosition -= 40;

                // Tableau des produits
                drawTable(contentStream, fontBold, fontRegular, fontSize, margin, yPosition,
                        productName, quantity, unitPrice);
            }

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            document.save(baos);
            return baos.toByteArray();
        }
    }

    private void addTextLine(PDPageContentStream contentStream, PDType1Font font, float fontSize,
            float x, float y, String text) throws IOException {
        contentStream.beginText();
        contentStream.setFont(font, fontSize);
        contentStream.newLineAtOffset(x, y);
        contentStream.showText(text);
        contentStream.endText();
    }

    private void drawTable(PDPageContentStream contentStream, PDType1Font fontBold,
            PDType1Font fontRegular, float fontSize, float margin, float yPosition,
            String productName, int quantity, double unitPrice) throws IOException {

        double total = quantity * unitPrice;

        // En-têtes
        addTextLine(contentStream, fontBold, fontSize, margin, yPosition, "Produit");
        addTextLine(contentStream, fontBold, fontSize, margin + 200, yPosition, "Qté");
        addTextLine(contentStream, fontBold, fontSize, margin + 280, yPosition, "Prix unitaire");
        addTextLine(contentStream, fontBold, fontSize, margin + 400, yPosition, "Total");

        yPosition -= 20;

        // Ligne de séparation
        contentStream.moveTo(margin, yPosition);
        contentStream.lineTo(margin + 450, yPosition);
        contentStream.stroke();

        yPosition -= 20;

        // Données du produit
        addTextLine(contentStream, fontRegular, fontSize, margin, yPosition, productName);
        addTextLine(contentStream, fontRegular, fontSize, margin + 200, yPosition, String.valueOf(quantity));
        addTextLine(contentStream, fontRegular, fontSize, margin + 280, yPosition, String.format("%.2f €", unitPrice));
        addTextLine(contentStream, fontRegular, fontSize, margin + 400, yPosition, String.format("%.2f €", total));

        yPosition -= 40;

        // Total final
        addTextLine(contentStream, fontBold, fontSize + 2, margin + 320, yPosition,
                String.format("TOTAL: %.2f €", total));
    }
}