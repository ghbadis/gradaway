package utils;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;
import java.util.Random;

public class EmailService {
    private static final String EMAIL = "awaygrad@gmail.com"; // Remplacez par votre email
    private static final String PASSWORD = "dmzq fnaw uglm glyn"; // Remplacez par votre mot de passe d'application Gmail
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

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(EMAIL));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail));
            message.setSubject("Code de réinitialisation de mot de passe");
            message.setText("Votre code de réinitialisation de mot de passe est : " + otp);

            Transport.send(message);
        } catch (MessagingException e) {
            e.printStackTrace();
            throw new RuntimeException("Erreur lors de l'envoi de l'email");
        }
    }
    
    public static void sendCandidatureConfirmationEmail(String toEmail, String universiteName, String domaine, String submissionDate) {
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

        try {
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
            System.out.println("Email de confirmation de candidature envoyé à " + toEmail);
        } catch (MessagingException e) {
            e.printStackTrace();
            throw new RuntimeException("Erreur lors de l'envoi de l'email de confirmation de candidature: " + e.getMessage());
        }
    }
} 