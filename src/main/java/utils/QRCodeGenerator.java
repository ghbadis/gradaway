package utils;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import javafx.scene.image.Image;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

public class QRCodeGenerator {

    /**
     * Génère une image de code QR à partir d'un texte
     * @param text Le texte à encoder dans le code QR
     * @param width Largeur de l'image
     * @param height Hauteur de l'image
     * @return Image JavaFX du code QR
     * @throws WriterException Si une erreur survient lors de la génération du code QR
     * @throws IOException Si une erreur survient lors de la conversion de l'image
     */
    public static Image generateQRCodeImage(String text, int width, int height) 
            throws WriterException, IOException {
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        Map<EncodeHintType, Object> hints = new HashMap<>();
        hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
        hints.put(EncodeHintType.MARGIN, 2); // Augmenter la marge pour une meilleure lisibilité
        hints.put(EncodeHintType.CHARACTER_SET, "UTF-8"); // Assurer l'encodage correct des caractères
        
        BitMatrix bitMatrix = qrCodeWriter.encode(text, BarcodeFormat.QR_CODE, width, height, hints);
        BufferedImage bufferedImage = MatrixToImageWriter.toBufferedImage(bitMatrix);
        
        // Convertir BufferedImage en Image JavaFX
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ImageIO.write(bufferedImage, "png", outputStream);
        ByteArrayInputStream inputStream = new ByteArrayInputStream(outputStream.toByteArray());
        
        return new Image(inputStream);
    }
    
    /**
     * Génère un fichier de code QR à partir d'un texte et le sauvegarde dans le dossier spécifié
     * @param text Le texte à encoder dans le code QR
     * @param width Largeur de l'image
     * @param height Hauteur de l'image
     * @param filePath Chemin du fichier où sauvegarder le code QR
     * @throws WriterException Si une erreur survient lors de la génération du code QR
     * @throws IOException Si une erreur survient lors de la sauvegarde de l'image
     */
    public static void generateQRCodeFile(String text, int width, int height, String filePath) 
            throws WriterException, IOException {
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        Map<EncodeHintType, Object> hints = new HashMap<>();
        hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
        hints.put(EncodeHintType.MARGIN, 2); // Augmenter la marge pour une meilleure lisibilité
        hints.put(EncodeHintType.CHARACTER_SET, "UTF-8"); // Assurer l'encodage correct des caractères
        
        BitMatrix bitMatrix = qrCodeWriter.encode(text, BarcodeFormat.QR_CODE, width, height, hints);
        
        // Créer le dossier parent si nécessaire
        Path path = Paths.get(filePath);
        Files.createDirectories(path.getParent());
        
        // Sauvegarder l'image
        MatrixToImageWriter.writeToPath(bitMatrix, "PNG", path);
    }
    
    /**
     * Génère une image BufferedImage de code QR à partir d'un texte
     * @param text Le texte à encoder dans le code QR
     * @param width Largeur de l'image
     * @param height Hauteur de l'image
     * @return BufferedImage du code QR
     * @throws WriterException Si une erreur survient lors de la génération du code QR
     * @throws IOException Si une erreur survient lors de la conversion de l'image
     */
    public static BufferedImage generateQRCodeBufferedImage(String text, int width, int height) 
            throws WriterException, IOException {
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        Map<EncodeHintType, Object> hints = new HashMap<>();
        hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
        hints.put(EncodeHintType.MARGIN, 2);
        hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");
        
        BitMatrix bitMatrix = qrCodeWriter.encode(text, BarcodeFormat.QR_CODE, width, height, hints);
        return MatrixToImageWriter.toBufferedImage(bitMatrix);
    }
    
    /**
     * Génère une chaîne Base64 optimisée pour les emails HTML à partir d'un texte
     * @param text Le texte à encoder dans le code QR
     * @param width Largeur de l'image
     * @param height Hauteur de l'image
     * @return Chaîne Base64 optimisée pour les emails HTML
     * @throws WriterException Si une erreur survient lors de la génération du code QR
     * @throws IOException Si une erreur survient lors de la conversion de l'image
     */
    public static String generateQRCodeBase64ForEmail(String text, int width, int height) 
            throws WriterException, IOException {
        BufferedImage bufferedImage = generateQRCodeBufferedImage(text, width, height);
        
        // Optimiser l'image pour l'email
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ImageIO.write(bufferedImage, "png", outputStream);
        
        byte[] imageBytes = outputStream.toByteArray();
        return java.util.Base64.getEncoder().encodeToString(imageBytes);
    }
}
