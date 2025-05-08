package utils;

import javax.activation.DataHandler;
import javax.mail.*;
import javax.mail.internet.*;
import javax.imageio.ImageIO;
import javax.mail.util.ByteArrayDataSource;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

public class EmailSender {
    
    // SMTP server configuration
    private static final String SMTP_HOST = "smtp.gmail.com";
    private static final String SMTP_PORT = "587";
    
    // Sender email credentials (you should replace these with your actual email and app password)
    private static final String SENDER_EMAIL = "awaygrad@gmail.com";
    private static final String SENDER_PASSWORD = "dmzq fnaw uglm glyn";
    
    /**
     * Sends an email to the specified recipient
     * 
     * @param recipient Email address of the recipient
     * @param subject Subject of the email
     * @param htmlContent HTML content of the email
     * @return true if the email was sent successfully, false otherwise
     */
    public static boolean sendEmail(String recipient, String subject, String htmlContent) {
        return sendEmail(recipient, subject, htmlContent, null, null);
    }
    
    /**
     * Sends an email to the specified recipient with an optional QR code attachment
     * 
     * @param recipient Email address of the recipient
     * @param subject Subject of the email
     * @param htmlContent HTML content of the email
     * @param qrCodeContent Content to encode in the QR code (if null, no QR code is attached)
     * @param qrCodeFileName Name of the QR code file attachment (if null, a default name is used)
     * @return true if the email was sent successfully, false otherwise
     */
    public static boolean sendEmail(String recipient, String subject, String htmlContent, 
                                   String qrCodeContent, String qrCodeFileName) {
        // Set mail properties
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", SMTP_HOST);
        props.put("mail.smtp.port", SMTP_PORT);
        
        // Create session with authenticator
        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(SENDER_EMAIL, SENDER_PASSWORD);
            }
        });
        
        try {
            // Create message
            MimeMessage message = new MimeMessage(session);
            message.setFrom(new InternetAddress(SENDER_EMAIL));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipient));
            message.setSubject(subject);
            
            // Create the message body part
            MimeBodyPart messageBodyPart = new MimeBodyPart();
            messageBodyPart.setContent(htmlContent, "text/html; charset=utf-8");
            
            // Create a multipart message
            Multipart multipart = new MimeMultipart();
            multipart.addBodyPart(messageBodyPart);
            
            // Add QR code attachment if content is provided
            if (qrCodeContent != null && !qrCodeContent.isEmpty()) {
                try {
                    // Generate QR code image
                    BufferedImage qrImage = QRCodeGeneratorF.generateQRCodeBufferedImage(qrCodeContent, 300, 300);
                    
                    // Convert to byte array
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    ImageIO.write(qrImage, "PNG", baos);
                    byte[] imageBytes = baos.toByteArray();
                    
                    // Create attachment part
                    MimeBodyPart attachmentPart = new MimeBodyPart();
                    attachmentPart.setDataHandler(new DataHandler(
                            new ByteArrayDataSource(imageBytes, "image/png")));
                    
                    // Set filename
                    String fileName = (qrCodeFileName != null && !qrCodeFileName.isEmpty()) 
                            ? qrCodeFileName : "reservation_qr_code.png";
                    attachmentPart.setFileName(fileName);
                    
                    // Add to multipart
                    multipart.addBodyPart(attachmentPart);
                    
                    System.out.println("QR code attachment added to email");
                } catch (Exception e) {
                    System.err.println("Error creating QR code attachment: " + e.getMessage());
                    e.printStackTrace();
                }
            }
            
            // Set the complete message parts
            message.setContent(multipart);
            
            // Send message
            Transport.send(message);
            System.out.println("Email sent successfully to: " + recipient);
            return true;
        } catch (MessagingException e) {
            System.err.println("Failed to send email: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Generates a QR code as a Base64 encoded string for embedding in HTML
     * Cette méthode est maintenant dépréciée, utilisez QRCodeGenerator.generateQRCodeBase64ForEmail à la place
     * 
     * @param content The content to encode in the QR code
     * @param width The width of the QR code image
     * @param height The height of the QR code image
     * @return Base64 encoded string of the QR code image
     * @throws WriterException If there is an error generating the QR code
     * @throws IOException If there is an error converting the image
     * @deprecated Utilisez QRCodeGenerator.generateQRCodeBase64ForEmail à la place
     */
    @Deprecated
    public static String generateQRCodeBase64(String content, int width, int height) throws WriterException, IOException {
        // Déléguer à la nouvelle méthode optimisée
        return QRCodeGeneratorF.generateQRCodeBase64ForEmail(content, width, height);
    }
    
    /**
     * Generates HTML content for reservation confirmation email with QR code
     * 
     * @param nomFoyer Name of the foyer
     * @param dateDebut Start date of the reservation
     * @param dateFin End date of the reservation
     * @param dateReservation Date when the reservation was made
     * @param idEtudiant Student ID for the reservation
     * @param ville City of the foyer
     * @return HTML content as a String
     */
    /**
     * Génère le contenu QR pour une réservation
     * @param nomFoyer Nom du foyer
     * @param dateDebut Date de début
     * @param dateFin Date de fin
     * @param idEtudiant ID de l'étudiant
     * @param ville Ville du foyer
     * @return Contenu du code QR
     */
    public static String generateQRContent(String nomFoyer, String dateDebut, 
                                        String dateFin, int idEtudiant, String ville) {
        return "Réservation Foyer\n" +
                "ID Étudiant: " + idEtudiant + "\n" +
                "Foyer: " + nomFoyer + "\n" +
                "Ville: " + ville + "\n" +
                "Date début: " + dateDebut + "\n" +
                "Date fin: " + dateFin;
    }
    
    public static String generateReservationConfirmationEmail(String nomFoyer, String dateDebut, 
                                                           String dateFin, String dateReservation,
                                                           int idEtudiant, String ville) {
        // Le code QR sera envoyé comme pièce jointe, pas besoin de l'intégrer dans le HTML
        return "<!DOCTYPE html>"
             + "<html>"
             + "<head>"
             + "    <meta charset='UTF-8'>"
             + "    <style>"
             + "        body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; }"
             + "        .container { max-width: 600px; margin: 0 auto; padding: 20px; }"
             + "        .header { background-color: #4CAF50; color: white; padding: 10px; text-align: center; }"
             + "        .content { padding: 20px; border: 1px solid #ddd; }"
             + "        .footer { text-align: center; margin-top: 20px; font-size: 12px; color: #777; }"
             + "        table { width: 100%; border-collapse: collapse; margin: 20px 0; }"
             + "        th, td { padding: 10px; text-align: left; border-bottom: 1px solid #ddd; }"
             + "        th { background-color: #f2f2f2; }"
             + "        .qr-code { text-align: center; margin: 20px 0; }"
             + "    </style>"
             + "</head>"
             + "<body>"
             + "    <div class='container'>"
             + "        <div class='header'>"
             + "            <h1>Confirmation de Réservation</h1>"
             + "        </div>"
             + "        <div class='content'>"
             + "            <p>Cher(e) étudiant(e),</p>"
             + "            <p>Nous sommes heureux de vous confirmer que votre réservation au foyer a été confirmée.</p>"
             + "            <h2>Détails de la réservation :</h2>"
             + "            <table>"
             + "                <tr><th>ID Étudiant</th><td>" + idEtudiant + "</td></tr>"
             + "                <tr><th>Foyer</th><td>" + nomFoyer + "</td></tr>"
             + "                <tr><th>Ville</th><td>" + ville + "</td></tr>"
             + "                <tr><th>Date de début</th><td>" + dateDebut + "</td></tr>"
             + "                <tr><th>Date de fin</th><td>" + dateFin + "</td></tr>"
             + "                <tr><th>Date de réservation</th><td>" + dateReservation + "</td></tr>"
             + "            </table>"
             + "            <div class='qr-code' style='text-align: center; margin: 20px 0;'>\n"
             + "                <h3 style='color: #4CAF50;'>Votre code QR de réservation :</h3>\n"
             + "                <p><strong>Vous trouverez votre code QR de réservation en pièce jointe de cet email.</strong></p>\n"
             + "                <p><strong>Présentez ce code QR lors de votre arrivée au foyer.</strong></p>\n"
             + "            </div>"
             + "            <p>Nous vous remercions pour votre réservation et sommes impatients de vous accueillir.</p>"
             + "            <p>Cordialement,<br>L'équipe du foyer</p>"
             + "        </div>"
             + "        <div class='footer'>"
             + "            <p>Ceci est un email automatique, merci de ne pas y répondre.</p>"
             + "        </div>"
             + "    </div>"
             + "</body>"
             + "</html>";
    }
    
    /**
     * Generates HTML content for reservation confirmation email without QR code
     * (Legacy method for backward compatibility)
     * 
     * @param nomFoyer Name of the foyer
     * @param dateDebut Start date of the reservation
     * @param dateFin End date of the reservation
     * @param dateReservation Date when the reservation was made
     * @return HTML content as a String
     */
    public static String generateReservationConfirmationEmail(String nomFoyer, String dateDebut, 
                                                           String dateFin, String dateReservation) {
        // Call the new method with default values
        return generateReservationConfirmationEmail(nomFoyer, dateDebut, dateFin, dateReservation, 0, "");
    }

    public static String generateDossierConfirmationEmail(int idDossier, String dateDepot) {
        return "<!DOCTYPE html>"
             + "<html>"
             + "<head>"
             + "    <style>"
             + "        body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; }"
             + "        .container { max-width: 600px; margin: 0 auto; padding: 20px; }"
             + "        .header { background-color: #4CAF50; color: white; padding: 20px; text-align: center; }"
             + "        .content { padding: 20px; background-color: #f9f9f9; }"
             + "        .footer { text-align: center; padding: 20px; font-size: 12px; color: #666; }"
             + "        table { width: 100%; border-collapse: collapse; margin: 20px 0; }"
             + "        th, td { padding: 10px; border: 1px solid #ddd; text-align: left; }"
             + "        th { background-color: #f5f5f5; }"
             + "    </style>"
             + "</head>"
             + "<body>"
             + "    <div class='container'>"
             + "        <div class='header'>"
             + "            <h1>Confirmation de dépôt de dossier</h1>"
             + "        </div>"
             + "        <div class='content'>"
             + "            <p>Cher étudiant,</p>"
             + "            <p>Nous vous confirmons que votre dossier a été déposé avec succès.</p>"
             + "            <table>"
             + "                <tr><th>Numéro de dossier</th><td>" + idDossier + "</td></tr>"
             + "                <tr><th>Date de dépôt</th><td>" + dateDepot + "</td></tr>"
             + "            </table>"
             + "            <div class='qr-code' style='text-align: center; margin: 20px 0;'>\n"
             + "                <h3 style='color: #4CAF50;'>Votre code QR de dossier :</h3>\n"
             + "                <p><strong>Vous trouverez votre code QR de dossier en pièce jointe de cet email.</strong></p>\n"
             + "                <p><strong>Conservez ce code QR pour vos références futures.</strong></p>\n"
             + "            </div>"
             + "            <p>Nous vous remercions pour votre confiance.</p>"
             + "            <p>Cordialement,<br>L'équipe GradAway</p>"
             + "        </div>"
             + "        <div class='footer'>"
             + "            <p>Ceci est un email automatique, merci de ne pas y répondre.</p>"
             + "        </div>"
             + "    </div>"
             + "</body>"
             + "</html>";
    }

    public static String generateDossierQRContent(int idDossier, String dateDepot) {
        return "Dossier GradAway\n" +
               "ID Dossier: " + idDossier + "\n" +
               "Date de dépôt: " + dateDepot;
    }
}