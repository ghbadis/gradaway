package utils;

import javax.mail.*;
import javax.mail.internet.*;
import java.util.Properties;

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
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(SENDER_EMAIL));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipient));
            message.setSubject(subject);
            
            // Set the HTML content
            message.setContent(htmlContent, "text/html; charset=utf-8");
            
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
     * Generates HTML content for reservation confirmation email
     * 
     * @param nomFoyer Name of the foyer
     * @param dateDebut Start date of the reservation
     * @param dateFin End date of the reservation
     * @param dateReservation Date when the reservation was made
     * @return HTML content as a String
     */
    public static String generateReservationConfirmationEmail(String nomFoyer, String dateDebut, 
                                                           String dateFin, String dateReservation) {
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
             + "                <tr><th>Foyer</th><td>" + nomFoyer + "</td></tr>"
             + "                <tr><th>Date de début</th><td>" + dateDebut + "</td></tr>"
             + "                <tr><th>Date de fin</th><td>" + dateFin + "</td></tr>"
             + "                <tr><th>Date de réservation</th><td>" + dateReservation + "</td></tr>"
             + "            </table>"
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
} 