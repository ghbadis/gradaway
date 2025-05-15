package utils;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.util.ByteArrayDataSource;
import java.util.Properties;
import java.util.Random;
import com.google.zxing.WriterException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.DriverManager;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.File;

public class EmailService {
    // Email configuration - in a production environment, these should be read from environment variables
    private static final String EMAIL = "awaygrad@gmail.com"; 
    private static final String PASSWORD = "dmzq fnaw uglm glyn"; // Should be stored as an environment variable in production
    private static final String HOST = "smtp.gmail.com";
    private static final String PORT = "587";

    public static String generateOTP() {
        Random random = new Random();
        StringBuilder otp = new StringBuilder();
        for (int i = 0; i < 5; i++) {
            otp.append(random.nextInt(10));
        }
        return otp.toString();
    }

    public static void sendOTPEmail(String toEmail, String otp) {
        try {
            // Get user name from database or use a default
            String userName = null;
            try {
                userName = getUserNameByEmail(toEmail);
            } catch (SQLException e) {
                System.err.println("Error retrieving user name: " + e.getMessage());
            }
            
            if (userName == null || userName.isEmpty()) {
                userName = "Étudiant";
            }
            
            // Email properties and session
            Properties props = new Properties();
            props.put("mail.smtp.auth", "true");
            props.put("mail.smtp.starttls.enable", "true");
            props.put("mail.smtp.host", HOST);
            props.put("mail.smtp.port", PORT);
            props.put("mail.smtp.ssl.trust", HOST);

            Session session = Session.getInstance(props, new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(EMAIL, PASSWORD);
                }
            });

            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(EMAIL, "GradAway"));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail));
            message.setSubject("Code de réinitialisation de mot de passe - GradAway");
            
            // Create message content for OTP email
            String messageContent = "<p>Vous avez demandé une réinitialisation de votre mot de passe.</p>" +
                                    "<p><strong>Votre code de réinitialisation est:</strong></p>" +
                                    "<div style='font-size: 24px; font-weight: bold; text-align: center; " +
                                    "padding: 15px; margin: 20px 0; background-color: #f0f0f0; border-radius: 5px;'>" + 
                                    otp + "</div>" +
                                    "<p>Ce code est valable pour une utilisation unique et expirera prochainement.</p>" +
                                    "<p>Si vous n'avez pas demandé cette réinitialisation, veuillez ignorer cet email ou contacter notre support.</p>";
            
            // Create custom HTML without the standard candidature details
            StringBuilder html = new StringBuilder();
            
            html.append("<!DOCTYPE html><html><head><meta charset='UTF-8'>");
            html.append("<style>");
            html.append("body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; }");
            html.append(".container { width: 100%; max-width: 650px; margin: 0 auto; }");
            html.append(".header { background-color: #1A3473; color: white; padding: 20px; text-align: center; }");
            html.append(".content { padding: 20px; }");
            html.append(".footer { background-color: #f5f5f5; padding: 15px; text-align: center; font-size: 12px; }");
            html.append("</style></head><body>");
            
            html.append("<div class='container'>");
            html.append("<div class='header'><h1>GradAway - Réinitialisation de mot de passe</h1></div>");
            html.append("<div class='content'>");
            
            html.append("<p>Bonjour ").append(userName).append(",</p>");
            html.append(messageContent);
            
            html.append("<p>Cordialement,<br>L'équipe GradAway</p>");
            
            html.append("</div>"); // end content
            html.append("<div class='footer'>");
            html.append("© ").append(java.time.Year.now().getValue()).append(" GradAway. Tous droits réservés.");
            html.append("</div>"); // end footer
            html.append("</div>"); // end container
            
            html.append("</body></html>");
            
            // Set content
            message.setContent(html.toString(), "text/html; charset=utf-8");

            Transport.send(message);
            
            System.out.println("OTP email sent successfully to: " + toEmail);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Erreur lors de l'envoi de l'email");
        }
    }
    
    public static void sendCandidatureConfirmationEmail(String toEmail, String universiteName, String domaine, String submissionDate) {
        try {
            // Get user name from database
            String userName = getUserNameByEmail(toEmail);
            if (userName == null || userName.isEmpty()) {
                userName = "Étudiant";
            }

            // Email properties and session
            Properties props = new Properties();
            props.put("mail.smtp.auth", "true");
            props.put("mail.smtp.starttls.enable", "true");
            props.put("mail.smtp.host", HOST);
            props.put("mail.smtp.port", PORT);
            props.put("mail.smtp.ssl.trust", HOST);

            // Create a session with authentication
            Session session = Session.getInstance(props, new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(EMAIL, PASSWORD);
                }
            });
            
            // Create message
            MimeMessage message = new MimeMessage(session);
            message.setFrom(new InternetAddress(EMAIL, "GradAway"));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail));
            message.setSubject("Confirmation de Candidature - " + universiteName);
            
            // Create the HTML content without PDF references
            String htmlContent = createSimpleHtmlEmailContent(userName, universiteName, domaine, submissionDate);
            
            // Set content directly - no attachments needed
            message.setContent(htmlContent, "text/html; charset=utf-8");
            
            // Send the message
            Transport.send(message);
            
            System.out.println("Email sent successfully to: " + toEmail);
            
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Failed to send email: " + e.getMessage());
            
            // Try sending a simple text email as fallback
            try {
                sendSimpleCandidatureConfirmationEmail(toEmail, universiteName, domaine, submissionDate);
            } catch (Exception ex) {
                System.err.println("Even simple email failed: " + ex.getMessage());
            }
        }
    }
    
    // Create a standardized HTML email template for all types of candidature emails
    private static String createStandardHtmlEmailTemplate(String userName, String universiteName, String domaine, 
                                                    String submissionDate, String emailType, String messageContent) {
        StringBuilder html = new StringBuilder();
        
        html.append("<!DOCTYPE html><html><head><meta charset='UTF-8'>");
        html.append("<style>");
        html.append("body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; }");
        html.append(".container { width: 100%; max-width: 650px; margin: 0 auto; }");
        html.append(".header { background-color: #1A3473; color: white; padding: 20px; text-align: center; }");
        html.append(".content { padding: 20px; }");
        html.append(".footer { background-color: #f5f5f5; padding: 15px; text-align: center; font-size: 12px; }");
        html.append(".details { margin: 20px 0; padding: 15px; background-color: #f9f9f9; border-left: 4px solid #1A3473; }");
        html.append("</style></head><body>");
        
        html.append("<div class='container'>");
        html.append("<div class='header'><h1>GradAway - ").append(emailType).append("</h1></div>");
        html.append("<div class='content'>");
        
        html.append("<p>Bonjour ").append(userName).append(",</p>");
        html.append(messageContent);
        
        html.append("<div class='details'>");
        html.append("<p><strong>Détails de la candidature:</strong></p>");
        html.append("<p>- Université: ").append(universiteName).append("<br>");
        html.append("- Domaine d'étude: ").append(domaine).append("<br>");
        if (submissionDate != null && !submissionDate.isEmpty()) {
            html.append("- Date de soumission: ").append(submissionDate).append("<br>");
        }
        html.append("</p>");
        html.append("</div>");
        
        html.append("<p>Cordialement,<br>L'équipe GradAway</p>");
        
        html.append("</div>"); // end content
        html.append("<div class='footer'>");
        html.append("© ").append(java.time.Year.now().getValue()).append(" GradAway. Tous droits réservés.");
        html.append("</div>"); // end footer
        html.append("</div>"); // end container
        
        html.append("</body></html>");
        
        return html.toString();
    }
    
    private static String createSimpleHtmlEmailContent(String userName, String universiteName, String domaine, 
                                                 String submissionDate) {
        String messageContent = "<p>Nous confirmons que votre candidature a bien été soumise.</p>" +
                               "<p>L'équipe GradAway examinera votre candidature et vous tiendra informé de son évolution.</p>";
                               
        return createStandardHtmlEmailTemplate(userName, universiteName, domaine, submissionDate, 
                                              "Confirmation de Candidature", messageContent);
    }
    
    // Fallback method to send simple text email without HTML and QR code
    private static void sendSimpleCandidatureConfirmationEmail(String toEmail, String universiteName, 
                                                          String domaine, String submissionDate) 
                                                          throws MessagingException {
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", HOST);
        props.put("mail.smtp.port", PORT);

        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(EMAIL, PASSWORD);
            }
        });
        
        Message message = new MimeMessage(session);
        message.setFrom(new InternetAddress(EMAIL));
        message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail));
        message.setSubject("Confirmation de Candidature - GradAway");
        
        // Create email content
        StringBuilder emailContent = new StringBuilder();
        emailContent.append("Bonjour,\n\n");
        emailContent.append("Nous confirmons que votre candidature a bien été soumise.\n\n");
        emailContent.append("Détails de la candidature:\n");
        emailContent.append("- Université: ").append(universiteName).append("\n");
        emailContent.append("- Domaine d'étude: ").append(domaine).append("\n");
        emailContent.append("- Date de soumission: ").append(submissionDate).append("\n\n");
        emailContent.append("L'équipe GradAway examinera votre candidature et vous tiendra informé de son évolution.\n\n");
        emailContent.append("Cordialement,\n");
        emailContent.append("L'équipe GradAway");
        
        message.setText(emailContent.toString());
        
        Transport.send(message);
        
        System.out.println("Email de confirmation simplifié envoyé avec succès à " + toEmail);
    }
    
    private static String getUserNameByEmail(String email) throws SQLException {
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        String userName = null;
        
        try {
            // Connect to the database
            String url = "jdbc:mysql://localhost:3306/gradaway";
            String user = "root";
            String password = "";
            connection = DriverManager.getConnection(url, user, password);
            
            // Query to get user's name from email
            String query = "SELECT nom FROM user WHERE email = ?";
            statement = connection.prepareStatement(query);
            statement.setString(1, email);
            resultSet = statement.executeQuery();
            
            if (resultSet.next()) {
                userName = resultSet.getString("nom");
            }
        } finally {
            // Close resources
            if (resultSet != null) resultSet.close();
            if (statement != null) statement.close();
            if (connection != null) connection.close();
        }
        
        return userName;
    }
    
    private static String getUniversityImagePath(String universiteName) throws SQLException {
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        String imagePath = null;
        
        try {
            // Connect to the database
            String url = "jdbc:mysql://localhost:3306/gradaway";
            String user = "root";
            String password = "";
            connection = DriverManager.getConnection(url, user, password);
            
            // Query to get university's image path
            String query = "SELECT photoPath FROM universite WHERE Nom = ?";
            statement = connection.prepareStatement(query);
            statement.setString(1, universiteName);
            resultSet = statement.executeQuery();
            
            if (resultSet.next()) {
                imagePath = resultSet.getString("photoPath");
            }
        } finally {
            // Close resources
            if (resultSet != null) resultSet.close();
            if (statement != null) statement.close();
            if (connection != null) connection.close();
        }
        
        return imagePath;
    }
    
    public static void sendCandidatureAcceptationEmail(String toEmail, String universiteName, String domaine) {
        try {
            // Get user name from database
            String userName = getUserNameByEmail(toEmail);
            if (userName == null || userName.isEmpty()) {
                userName = "Étudiant";
            }

            // Email properties and session
            Properties props = new Properties();
            props.put("mail.smtp.auth", "true");
            props.put("mail.smtp.starttls.enable", "true");
            props.put("mail.smtp.host", HOST);
            props.put("mail.smtp.port", PORT);
            props.put("mail.smtp.ssl.trust", HOST);

            Session session = Session.getInstance(props, new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(EMAIL, PASSWORD);
                }
            });

            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(EMAIL, "GradAway"));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail));
            message.setSubject("Acceptation de Candidature - " + universiteName);
            
            // Create HTML content for acceptance email
            String messageContent = "<p>Nous avons le plaisir de vous informer que votre candidature a été acceptée.</p>" +
                                   "<p>Félicitations! Nous vous contacterons prochainement pour les prochaines étapes.</p>";
            
            String htmlContent = createStandardHtmlEmailTemplate(userName, universiteName, domaine, null, 
                                                               "Acceptation de Candidature", messageContent);
            
            // Set content
            message.setContent(htmlContent, "text/html; charset=utf-8");
            
            Transport.send(message);
            
            System.out.println("Email d'acceptation de candidature envoyé avec succès à " + toEmail);
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Erreur lors de l'envoi de l'email d'acceptation: " + e.getMessage());
            throw new RuntimeException("Erreur lors de l'envoi de l'email d'acceptation");
        }
    }
    
    public static void sendCandidatureRejectionEmail(String toEmail, String universiteName, String domaine) {
        try {
            // Get user name from database
            String userName = getUserNameByEmail(toEmail);
            if (userName == null || userName.isEmpty()) {
                userName = "Étudiant";
            }

            // Email properties and session
            Properties props = new Properties();
            props.put("mail.smtp.auth", "true");
            props.put("mail.smtp.starttls.enable", "true");
            props.put("mail.smtp.host", HOST);
            props.put("mail.smtp.port", PORT);
            props.put("mail.smtp.ssl.trust", HOST);

            Session session = Session.getInstance(props, new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(EMAIL, PASSWORD);
                }
            });

            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(EMAIL, "GradAway"));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail));
            message.setSubject("Candidature Non Retenue - " + universiteName);
            
            // Create HTML content for rejection email
            String messageContent = "<p>Nous regrettons de vous informer que votre candidature n'a pas été retenue.</p>" +
                                   "<p>Nous vous encourageons à explorer d'autres opportunités disponibles sur notre plateforme.</p>";
            
            String htmlContent = createStandardHtmlEmailTemplate(userName, universiteName, domaine, null, 
                                                               "Candidature Non Retenue", messageContent);
            
            // Set content
            message.setContent(htmlContent, "text/html; charset=utf-8");
            
            Transport.send(message);
            
            System.out.println("Email de refus de candidature envoyé avec succès à " + toEmail);
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Erreur lors de l'envoi de l'email de refus: " + e.getMessage());
            throw new RuntimeException("Erreur lors de l'envoi de l'email de refus");
        }
    }

    /**
     * Test method for verifying email functionality
     * This can be called from a controller for direct testing
     */
    public static boolean testSendPDF(String toEmail) {
        try {
            // Get user name (or use a default for testing)
            String userName = "Test User";
            try {
                userName = getUserNameByEmail(toEmail);
                if (userName == null || userName.isEmpty()) {
                    userName = "Test User";
                }
            } catch (Exception e) {
                // In case of database issues, continue with default name
                System.out.println("Could not retrieve user name: " + e.getMessage());
            }

            // Create a simple test PDF
            String tempFilePath = PDFGenerator.generateCandidatureCard(
                userName, "Test University", "Test Domain", "01/01/2023", null);
            File pdfFile = new File(tempFilePath);
            
            if (!pdfFile.exists()) {
                throw new IOException("Failed to create test PDF file");
            }
            
            // Set up email properties
            Properties props = new Properties();
            props.put("mail.smtp.auth", "true");
            props.put("mail.smtp.starttls.enable", "true");
            props.put("mail.smtp.host", HOST);
            props.put("mail.smtp.port", PORT);
            props.put("mail.smtp.ssl.trust", HOST);
            
            // Create session
            Session session = Session.getInstance(props, new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(EMAIL, PASSWORD);
                }
            });
            
            // Create message
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(EMAIL, "GradAway Test"));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail));
            message.setSubject("GradAway - Test Email");
            
            // Create test message content
            String messageContent = "<p>Ceci est un email de test contenant une pièce jointe PDF.</p>" +
                                   "<p>Si vous pouvez voir cet email correctement formaté, le système d'email fonctionne.</p>";
            
            String htmlContent = createStandardHtmlEmailTemplate(userName, "Test University", "Test Domain", 
                                                               "01/01/2023", "Test Email", messageContent);
            
            // Create multipart message
            Multipart multipart = new MimeMultipart();
            
            // HTML part
            MimeBodyPart htmlPart = new MimeBodyPart();
            htmlPart.setContent(htmlContent, "text/html; charset=utf-8");
            multipart.addBodyPart(htmlPart);
            
            // PDF attachment part
            MimeBodyPart attachmentPart = new MimeBodyPart();
            FileDataSource source = new FileDataSource(pdfFile);
            attachmentPart.setDataHandler(new DataHandler(source));
            attachmentPart.setFileName("GradAway_Test_Attachment.pdf");
            multipart.addBodyPart(attachmentPart);
            
            // Set content
            message.setContent(multipart);
            
            // Send message
            Transport.send(message);
            
            System.out.println("Test email sent successfully to: " + toEmail);
            
            // Clean up temporary file
            pdfFile.delete();
            
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Test email failed: " + e.getMessage());
            return false;
        }
    }
} 