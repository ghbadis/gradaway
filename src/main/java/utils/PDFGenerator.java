package utils;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfWriter;
import entities.Evenement;
import entities.ReservationEvenement;
import Services.ServiceEvenement;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Random;

public class PDFGenerator {
    private static final String PDF_DIR = "src/main/resources/web/billets/";

    static {
        // Créer le dossier s'il n'existe pas
        new File(PDF_DIR).mkdirs();
    }

    public static String generateBilletPDF(ReservationEvenement reservation) throws DocumentException, IOException, SQLException {
        // Générer un code secret aléatoire
        String codeSecret = generateSecretCode();
        
        // Créer le document PDF
        Document document = new Document();
        String fileName = "billet_" + reservation.getId_reservation() + ".pdf";
        String filePath = PDF_DIR + fileName;
        PdfWriter.getInstance(document, new FileOutputStream(filePath));
        
        document.open();
        
        // Ajouter le titre
        Font titleFont = new Font(Font.FontFamily.HELVETICA, 18, Font.BOLD);
        Paragraph title = new Paragraph("Billet d'entrée - Événement", titleFont);
        title.setAlignment(Element.ALIGN_CENTER);
        title.setSpacingAfter(20);
        document.add(title);
        
        // Récupérer les informations de l'événement
        ServiceEvenement serviceEvenement = new ServiceEvenement();
        Evenement evenement = null;
        for (Evenement ev : serviceEvenement.recuperer()) {
            if (ev.getId_evenement() == reservation.getId_evenement()) {
                evenement = ev;
                break;
            }
        }
        
        // Ajouter les informations
        Font normalFont = new Font(Font.FontFamily.HELVETICA, 12, Font.NORMAL);
        document.add(new Paragraph("Nom de l'événement: " + (evenement != null ? evenement.getNom() : "N/A"), normalFont));
        document.add(new Paragraph("Nom: " + reservation.getNom(), normalFont));
        document.add(new Paragraph("Prénom: " + reservation.getPrenom(), normalFont));
        document.add(new Paragraph("Email: " + reservation.getEmail(), normalFont));
        document.add(new Paragraph("Date: " + reservation.getDate(), normalFont));
        
        // Ajouter le code secret
        Font codeFont = new Font(Font.FontFamily.HELVETICA, 14, Font.BOLD);
        Paragraph codeParagraph = new Paragraph("Code secret: " + codeSecret, codeFont);
        codeParagraph.setAlignment(Element.ALIGN_CENTER);
        codeParagraph.setSpacingBefore(20);
        document.add(codeParagraph);
        
        document.close();
        
        // Retourner l'URL web du PDF en utilisant l'adresse IP locale
        return WebServer.getWebUrl() + fileName;
    }
    
    private static String generateSecretCode() {
        Random random = new Random();
        StringBuilder code = new StringBuilder();
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        
        // Générer un code de 8 caractères
        for (int i = 0; i < 8; i++) {
            code.append(chars.charAt(random.nextInt(chars.length())));
        }
        
        return code.toString();
    }
} 