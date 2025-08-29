package com.mon_projet_pfa.backend.services;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;
import org.springframework.stereotype.Service;

import com.mon_projet_pfa.backend.models.OrderItem;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;


@Service
public class PurchaseOrderPdfService {
public byte[] generatePurchaseOrderPdf(String supplierName, List<OrderItem> items, String status) throws IOException {
    try (PDDocument document = new PDDocument()) {
        PDPage page = new PDPage(PDRectangle.A4);
        document.addPage(page);

        try (PDPageContentStream contentStream = new PDPageContentStream(document, page)) {
            PDType1Font fontBold = new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD);
            PDType1Font fontRegular = new PDType1Font(Standard14Fonts.FontName.HELVETICA);

            float margin = 50;
            float yPosition = page.getMediaBox().getHeight() - margin;
            float fontSize = 12;

            // Titre
            contentStream.beginText();
            contentStream.setFont(fontBold, 18);
            contentStream.newLineAtOffset(margin, yPosition);
            contentStream.showText("BON DE COMMANDE - " + status);
            contentStream.endText();

            yPosition -= 40;

            // Fournisseur
            addTextLine(contentStream, fontRegular, fontSize, margin, yPosition, "Fournisseur: " + supplierName);
            yPosition -= 20;

            addTextLine(contentStream, fontRegular, fontSize, margin, yPosition,
                    "Date: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));
            yPosition -= 40;

            // En-têtes du tableau
            addTextLine(contentStream, fontBold, fontSize, margin, yPosition, "Produit");
            addTextLine(contentStream, fontBold, fontSize, margin + 200, yPosition, "Qté");
            addTextLine(contentStream, fontBold, fontSize, margin + 280, yPosition, "Prix unitaire");
            addTextLine(contentStream, fontBold, fontSize, margin + 400, yPosition, "Total");
            yPosition -= 20;

            contentStream.moveTo(margin, yPosition);
            contentStream.lineTo(page.getMediaBox().getWidth() - margin, yPosition);
            contentStream.stroke();
            yPosition -= 20;

            // Boucle sur tous les produits
            double totalGeneral = 0;
            for (OrderItem item : items) {
                addTextLine(contentStream, fontRegular, fontSize, margin, yPosition, item.getProduct().getName());
                addTextLine(contentStream, fontRegular, fontSize, margin + 200, yPosition, String.valueOf(item.getQuantity()));
                addTextLine(contentStream, fontRegular, fontSize, margin + 280, yPosition, String.format("%.2f €", item.getUnitPrice().doubleValue()));
                addTextLine(contentStream, fontRegular, fontSize, margin + 400, yPosition, String.format("%.2f €", item.getQuantity() * item.getUnitPrice().doubleValue()));

                totalGeneral += item.getQuantity() * item.getUnitPrice().doubleValue();
                yPosition -= 20;

                // saut de page si besoin
                if (yPosition < margin + 50) {
                    contentStream.close();
                    page = new PDPage(PDRectangle.A4);
                    document.addPage(page);
                    yPosition = page.getMediaBox().getHeight() - margin;
                }
            }

            yPosition -= 40;
            addTextLine(contentStream, fontBold, fontSize + 2, page.getMediaBox().getWidth() - margin - 150, yPosition,
                    "TOTAL: " + String.format("%.2f €", totalGeneral));
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