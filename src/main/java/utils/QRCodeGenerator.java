package utils;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

/**
 * Utility class for generating QR Codes for candidature information
 */
public class QRCodeGenerator {

    /**
     * Generate a QR code image for the given data string
     * 
     * @param data Text to encode in the QR code
     * @param width Width of the QR code
     * @param height Height of the QR code
     * @return BufferedImage of the generated QR code
     * @throws WriterException If encoding fails
     */
    public static BufferedImage generateQRCode(String data, int width, int height) throws WriterException {
        Map<EncodeHintType, Object> hints = new HashMap<>();
        hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
        hints.put(EncodeHintType.MARGIN, 1);
        
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        BitMatrix bitMatrix = qrCodeWriter.encode(data, BarcodeFormat.QR_CODE, width, height, hints);
        
        return MatrixToImageWriter.toBufferedImage(bitMatrix);
    }
    
    /**
     * Generate a QR code image as a Base64 encoded string for embedding in HTML
     * 
     * @param data Text to encode in the QR code
     * @param width Width of the QR code
     * @param height Height of the QR code
     * @return Base64 encoded string of the QR code image
     * @throws WriterException If encoding fails
     * @throws IOException If image processing fails
     */
    public static String generateQRCodeBase64(String data, int width, int height) throws WriterException, IOException {
        BufferedImage qrImage = generateQRCode(data, width, height);
        
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ImageIO.write(qrImage, "png", outputStream);
        
        return Base64.getEncoder().encodeToString(outputStream.toByteArray());
    }
    
    /**
     * Create a candidate card with university information and QR code
     * 
     * @param candidateName Name of the candidate
     * @param universityName Name of the university
     * @param domaine Field of study
     * @param date Submission date
     * @param universityImagePath Path to the university image
     * @return Base64 encoded string of the candidate card image
     * @throws IOException If image processing fails
     * @throws WriterException If QR code generation fails
     */
    public static String generateCandidateCard(String candidateName, String universityName, 
                                             String domaine, String date, String universityImagePath) 
                                             throws IOException, WriterException {
        // Create card dimensions
        int cardWidth = 600;
        int cardHeight = 350;
        
        // Create a blank image for the card with white background
        BufferedImage cardImage = new BufferedImage(cardWidth, cardHeight, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = cardImage.createGraphics();
        
        // Set background to gradient
        GradientPaint gradient = new GradientPaint(0, 0, new Color(25, 35, 115), // Dark blue
                                                  cardWidth, cardHeight, new Color(62, 146, 204)); // Lighter blue
        g2d.setPaint(gradient);
        g2d.fillRect(0, 0, cardWidth, cardHeight);
        
        // Set text color and font
        g2d.setColor(Color.WHITE);
        Font titleFont = new Font("Arial", Font.BOLD, 24);
        Font normalFont = new Font("Arial", Font.PLAIN, 16);
        Font smallFont = new Font("Arial", Font.PLAIN, 12);
        
        // Add GradAway logo/title
        g2d.setFont(titleFont);
        g2d.drawString("GradAway - Confirmation de Candidature", 20, 30);
        
        // Add university name
        g2d.setFont(new Font("Arial", Font.BOLD, 20));
        g2d.drawString(universityName, 20, 70);
        
        // Add candidate information
        g2d.setFont(normalFont);
        g2d.drawString("Candidat: " + candidateName, 20, 100);
        g2d.drawString("Domaine d'étude: " + domaine, 20, 130);
        g2d.drawString("Date de soumission: " + date, 20, 160);
        
        // Try to load university image
        BufferedImage universityImage = null;
        try {
            if (universityImagePath != null && !universityImagePath.isEmpty()) {
                File imgFile = new File("src/main/resources/" + universityImagePath);
                if (imgFile.exists()) {
                    universityImage = ImageIO.read(imgFile);
                }
            }
        } catch (Exception e) {
            System.err.println("Error loading university image: " + e.getMessage());
        }
        
        // If university image is found, add it to the card
        if (universityImage != null) {
            // Scale image to fit
            int imgWidth = 150;
            int imgHeight = 100;
            g2d.drawImage(universityImage, cardWidth - imgWidth - 20, 20, imgWidth, imgHeight, null);
        }
        
        // Generate QR code with all information
        String qrData = "Candidat: " + candidateName + "\n" +
                       "Université: " + universityName + "\n" +
                       "Domaine: " + domaine + "\n" +
                       "Date: " + date;
        
        BufferedImage qrCodeImage = generateQRCode(qrData, 150, 150);
        g2d.drawImage(qrCodeImage, cardWidth - 170, cardHeight - 170, null);
        
        // Add footer text
        g2d.setFont(smallFont);
        g2d.drawString("Ce QR code contient les détails de votre candidature.", 20, cardHeight - 20);
        
        g2d.dispose();
        
        // Convert card to Base64 for email embedding
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ImageIO.write(cardImage, "png", outputStream);
        
        return Base64.getEncoder().encodeToString(outputStream.toByteArray());
    }
} 