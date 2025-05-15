package utils;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * Utility class for generating PDF documents with candidature information
 */
public class PDFGenerator {

    // Define colors
    private static final BaseColor LIGHT_BLUE = new BaseColor(173, 216, 230); // Light blue
    private static final BaseColor DARK_BLUE = new BaseColor(26, 52, 115);    // Dark blue for text
    private static final BaseColor WHITE = BaseColor.WHITE;                   // White

    /**
     * Generate a PDF document with candidature information and QR code
     *
     * @param userName Name of the candidate
     * @param universityName Name of the university
     * @param domaine Field of study
     * @param submissionDate Submission date
     * @param universityImagePath Path to the university image
     * @return Generated PDF file path
     * @throws DocumentException If PDF generation fails
     * @throws IOException If file operations fail
     * @throws WriterException If QR code generation fails
     */
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
    
    /**
     * Add content to the PDF document
     */
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
    
    /**
     * Generate a QR code image for embedding in the PDF
     */
    private static Image generateQRCodeImage(String data, int width, int height) 
            throws WriterException, IOException, BadElementException {
        
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        Map<EncodeHintType, Object> hints = new HashMap<>();
        hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
        hints.put(EncodeHintType.MARGIN, 1);
        
        com.google.zxing.common.BitMatrix bitMatrix = qrCodeWriter.encode(
            data, BarcodeFormat.QR_CODE, width, height, hints);
        
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        MatrixToImageWriter.writeToStream(bitMatrix, "PNG", outputStream);
        
        return Image.getInstance(outputStream.toByteArray());
    }
    
    /**
     * Generate a PDF document and return its contents as base64 encoded string for email
     */
    public static String generateCandidatureCardBase64(String userName, String universityName, 
                                                    String domaine, String submissionDate, 
                                                    String universityImagePath)
                                                    throws DocumentException, IOException, WriterException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        
        // Create PDF document
        Document document = new Document(PageSize.A4);
        PdfWriter writer = PdfWriter.getInstance(document, outputStream);
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
        
        // Convert to Base64
        return java.util.Base64.getEncoder().encodeToString(outputStream.toByteArray());
    }
} 