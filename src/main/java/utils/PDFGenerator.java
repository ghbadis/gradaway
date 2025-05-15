package utils;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import com.google.zxing.*;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import entities.Evenement;
import entities.ReservationEvenement;
import Services.ServiceEvenement;

import java.io.*;
import java.net.URL;
import java.sql.SQLException;
import java.util.*;

public class PDFGenerator {
    private static final String PDF_DIR = "src/main/resources/web/billets/";
    private static final BaseColor LIGHT_BLUE = new BaseColor(173, 216, 230);
    private static final BaseColor DARK_BLUE = new BaseColor(26, 52, 115);
    private static final BaseColor WHITE = BaseColor.WHITE;

    static {
        new File(PDF_DIR).mkdirs();
    }

    public static String generateBilletPDF(ReservationEvenement reservation) throws DocumentException, IOException, SQLException, WriterException {
        String codeSecret = generateSecretCode();
        ServiceEvenement serviceEvenement = new ServiceEvenement();
        Evenement evenement = null;
        for (Evenement ev : serviceEvenement.recuperer()) {
            if (ev.getId_evenement() == reservation.getId_evenement()) {
                evenement = ev;
                break;
            }
        }

        String fileName = "billet_" + reservation.getId_reservation() + ".pdf";
        String filePath = PDF_DIR + fileName;
        Document document = new Document(PageSize.A4);
        PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(filePath));
        document.open();

        // Background
        PdfContentByte canvas = writer.getDirectContentUnder();
        Rectangle rect = new Rectangle(document.getPageSize());
        rect.setBackgroundColor(LIGHT_BLUE);
        canvas.rectangle(rect);

        // Title
        Font titleFont = new Font(Font.FontFamily.HELVETICA, 24, Font.BOLD, DARK_BLUE);
        Paragraph title = new Paragraph("Billet d'entrée - Événement", titleFont);
        title.setAlignment(Element.ALIGN_CENTER);
        title.setSpacingAfter(20);
        document.add(title);

        // Event info section
        Font whiteFont = new Font(Font.FontFamily.HELVETICA, 14, Font.NORMAL, WHITE);
        PdfPTable infoTable = new PdfPTable(1);
        infoTable.setWidthPercentage(90);
        infoTable.setSpacingBefore(15);
        infoTable.setSpacingAfter(15);
        PdfPCell infoCell = new PdfPCell();
        infoCell.setBackgroundColor(DARK_BLUE);
        infoCell.setPadding(15);
        infoCell.setBorderWidth(0);

        infoCell.addElement(new Paragraph("Nom de l'événement: " + (evenement != null ? evenement.getNom() : "N/A"), whiteFont));
        infoCell.addElement(new Paragraph("Nom: " + reservation.getNom(), whiteFont));
        infoCell.addElement(new Paragraph("Prénom: " + reservation.getPrenom(), whiteFont));
        infoCell.addElement(new Paragraph("Email: " + reservation.getEmail(), whiteFont));
        infoCell.addElement(new Paragraph("Date: " + reservation.getDate(), whiteFont));
        infoTable.addCell(infoCell);
        document.add(infoTable);

        // Code secret
        Font codeFont = new Font(Font.FontFamily.HELVETICA, 16, Font.BOLD, DARK_BLUE);
        Paragraph codeParagraph = new Paragraph("Code secret: " + codeSecret, codeFont);
        codeParagraph.setAlignment(Element.ALIGN_CENTER);
        codeParagraph.setSpacingBefore(10);
        document.add(codeParagraph);

        // QR Code
        String qrData = "Reservation ID: " + reservation.getId_reservation() + "\n" +
                "Nom: " + reservation.getNom() + "\n" +
                "Prénom: " + reservation.getPrenom() + "\n" +
                "Email: " + reservation.getEmail() + "\n" +
                "Événement: " + (evenement != null ? evenement.getNom() : "N/A") + "\n" +
                "Code Secret: " + codeSecret;
        Image qrImage = generateQRCodeImage(qrData, 200, 200);
        qrImage.setAlignment(Element.ALIGN_CENTER);
        document.add(qrImage);

        // Footer
        Font footerFont = new Font(Font.FontFamily.HELVETICA, 10, Font.ITALIC, DARK_BLUE);
        Paragraph footer = new Paragraph("Merci de présenter ce billet à l'entrée de l'événement.", footerFont);
        footer.setAlignment(Element.ALIGN_CENTER);
        footer.setSpacingBefore(20);
        document.add(footer);

        document.close();
        return WebServer.getWebUrl() + fileName;
    }

    public static String generateCandidatureCard(String userName, String universityName,
                                                 String domaine, String submissionDate,
                                                 String universityImagePath)
            throws DocumentException, IOException, WriterException {
        // Create a unique filename for the PDF
        String fileName = "candidature_" + System.currentTimeMillis() + ".pdf";
        String filePath = System.getProperty("java.io.tmpdir") + File.separator + fileName;

        // Create PDF document
        Document document = new Document(PageSize.A4);
        PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(filePath));
        document.open();

        // Set document metadata
        document.addTitle("Carte de Candidature - " + universityName);
        document.addSubject("Candidature pour " + universityName);
        document.addKeywords("gradaway, candidature, université, " + universityName);
        document.addCreator("GradAway Application");

        // Set background color
        PdfContentByte canvas = writer.getDirectContentUnder();
        Rectangle rect = new Rectangle(document.getPageSize());
        rect.setBackgroundColor(LIGHT_BLUE);
        canvas.rectangle(rect);

        // Add content
        addContent(document, userName, universityName, domaine, submissionDate, universityImagePath);

        document.close();

        return filePath;
    }
    private static void addContent(Document document, String userName, String universityName,
                                   String domaine, String submissionDate, String universityImagePath)
            throws DocumentException, IOException, WriterException {

        // Define fonts
        Font titleFont = new Font(Font.FontFamily.HELVETICA, 24, Font.BOLD, DARK_BLUE);
        Font headerFont = new Font(Font.FontFamily.HELVETICA, 20, Font.BOLD, DARK_BLUE);
        Font normalFont = new Font(Font.FontFamily.HELVETICA, 14, Font.NORMAL, DARK_BLUE);
        Font smallFont = new Font(Font.FontFamily.HELVETICA, 12, Font.ITALIC, DARK_BLUE);

        // Add card title
        Paragraph title = new Paragraph("Carte de Candidature", titleFont);
        title.setAlignment(Element.ALIGN_CENTER);
        title.setSpacingAfter(20);
        document.add(title);

        // Add university logo at top center if available
        if (universityImagePath != null && !universityImagePath.isEmpty()) {
            try {
                // Try to load from resources
                Image universityImage = null;
                try {
                    URL photoUrl = PDFGenerator.class.getResource("/" + universityImagePath);
                    if (photoUrl != null) {
                        universityImage = Image.getInstance(photoUrl);
                    } else {
                        // Try as a file path
                        File imgFile = new File("src/main/resources/" + universityImagePath);
                        if (imgFile.exists()) {
                            universityImage = Image.getInstance(imgFile.getAbsolutePath());
                        }
                    }
                } catch (Exception e) {
                    System.err.println("Error loading university image: " + e.getMessage());
                }

                if (universityImage != null) {
                    // Scale image
                    universityImage.scaleToFit(200, 150);
                    universityImage.setAlignment(Element.ALIGN_CENTER);
                    document.add(universityImage);

                    // Add some spacing
                    document.add(new Paragraph(" "));
                }
            } catch (Exception e) {
                System.err.println("Could not add university image: " + e.getMessage());
            }
        }

        // Create a table for university information with blue background
        PdfPTable infoTable = new PdfPTable(1);
        infoTable.setWidthPercentage(90);
        infoTable.setSpacingBefore(15);
        infoTable.setSpacingAfter(15);

        // Info cell with dark blue background
        PdfPCell infoCell = new PdfPCell();
        infoCell.setBackgroundColor(DARK_BLUE);
        infoCell.setPadding(15);
        infoCell.setBorderWidth(0);

        // University name
        Paragraph universityPara = new Paragraph(universityName, new Font(Font.FontFamily.HELVETICA, 20, Font.BOLD, WHITE));
        universityPara.setAlignment(Element.ALIGN_CENTER);
        universityPara.setSpacingAfter(15);
        infoCell.addElement(universityPara);

        // Horizontal line
        PdfPTable lineTable = new PdfPTable(1);
        lineTable.setWidthPercentage(80);
        PdfPCell lineCell = new PdfPCell(new Phrase(" "));
        lineCell.setBorderColorBottom(WHITE);
        lineCell.setBorderWidthBottom(1);
        lineCell.setBorder(Rectangle.BOTTOM);
        lineCell.setPaddingBottom(10);
        lineTable.addCell(lineCell);
        infoCell.addElement(lineTable);

        // Add candidate info
        Font whiteFont = new Font(Font.FontFamily.HELVETICA, 14, Font.NORMAL, WHITE);

        Paragraph candidatePara = new Paragraph("Candidat: " + userName, whiteFont);
        candidatePara.setSpacingAfter(10);
        infoCell.addElement(candidatePara);

        Paragraph domainePara = new Paragraph("Domaine d'étude: " + domaine, whiteFont);
        domainePara.setSpacingAfter(10);
        infoCell.addElement(domainePara);

        Paragraph datePara = new Paragraph("Date de soumission: " + submissionDate, whiteFont);
        datePara.setSpacingAfter(15);
        infoCell.addElement(datePara);

        infoTable.addCell(infoCell);
        document.add(infoTable);

        // Generate QR code with candidate information in the exact format needed
        String qrData = "Candidat: " + userName + "\n" +
                "Université: " + universityName + "\n" +
                "Domaine: " + domaine + "\n" +
                "Date: " + submissionDate;

        Image qrCode = generateQRCodeImage(qrData, 200, 200);
        qrCode.setAlignment(Element.ALIGN_CENTER);
        document.add(qrCode);

        // Add footer text
        Paragraph footerPara = new Paragraph("Ce QR code contient les détails de votre candidature. " +
                "Présentez-le lors de vos interactions avec l'université.", smallFont);
        footerPara.setAlignment(Element.ALIGN_CENTER);
        footerPara.setSpacingBefore(15);
        document.add(footerPara);

        // Add GradAway signature at bottom
        Paragraph signaturePara = new Paragraph("GradAway - Votre passerelle vers l'excellence académique",
                new Font(Font.FontFamily.HELVETICA, 10, Font.ITALIC, DARK_BLUE));
        signaturePara.setAlignment(Element.ALIGN_CENTER);
        signaturePara.setSpacingBefore(30);
        document.add(signaturePara);
    }

    private static String generateSecretCode() {
        Random random = new Random();
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        StringBuilder code = new StringBuilder();
        for (int i = 0; i < 8; i++) {
            code.append(chars.charAt(random.nextInt(chars.length())));
        }
        return code.toString();
    }

    private static Image generateQRCodeImage(String data, int width, int height)
            throws WriterException, IOException, BadElementException {
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        Map<EncodeHintType, Object> hints = new HashMap<>();
        hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
        hints.put(EncodeHintType.MARGIN, 1);
        com.google.zxing.common.BitMatrix bitMatrix = qrCodeWriter.encode(data, BarcodeFormat.QR_CODE, width, height, hints);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        MatrixToImageWriter.writeToStream(bitMatrix, "PNG", outputStream);
        return Image.getInstance(outputStream.toByteArray());
    }
}
