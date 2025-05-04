package utils;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

public class EmailUtil {
    private static final String HOST = "smtp.gmail.com";
    private static final String PORT = "587";
    private static final String USERNAME = " nabliaous005@gmail.com"; // Email de l'application
    private static final String PASSWORD = "gvzl awcg ldhf otxn"; // Mot de passe d'application fourni
    private static final String APP_NAME = "JavaFXApp"; // Nom de l'application

    public static void sendConfirmationEmail(String toEmail, String nom, String prenom, String nomEvenement, String date) {
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", HOST);
        props.put("mail.smtp.port", PORT);

        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(USERNAME, PASSWORD);
            }
        });

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(USERNAME));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail));
            message.setSubject("Confirmation de réservation - " + nomEvenement + " - " + APP_NAME);

            String emailContent = String.format(
                "Cher(e) %s %s,\n\n" +
                "Nous vous confirmons votre réservation pour l'événement suivant :\n\n" +
                "Événement : %s\n" +
                "Date : %s\n\n" +
                "Merci de votre inscription !\n\n" +
                "Cordialement,\n" +
                "L'équipe %s",
                prenom, nom, nomEvenement, date, APP_NAME
            );

            message.setText(emailContent);

            Transport.send(message);
            System.out.println("Email de confirmation envoyé avec succès à " + toEmail);
        } catch (MessagingException e) {
            System.out.println("Erreur lors de l'envoi de l'email : " + e.getMessage());
            e.printStackTrace();
        }
    }
} 