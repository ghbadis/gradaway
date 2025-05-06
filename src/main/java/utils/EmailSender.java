package utils;

import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.TextArea;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

/**
 * Classe utilitaire pour l'envoi d'emails
 */
public class EmailSender {
    
    // Mode simulation (ne nécessite pas de configuration SMTP)
    private static final boolean SIMULATION_MODE = false;
    
    // Constantes pour la configuration de l'email
    private static final String SMTP_HOST = "smtp.gmail.com";
    private static final String SMTP_PORT = "587";
    
    // ⚠️ IMPORTANT: Remplacez ces valeurs par vos propres informations d'authentification
    // Pour Gmail, vous devez créer un "mot de passe d'application" spécifique:
    // 1. Activez l'authentification à deux facteurs sur votre compte Google
    // 2. Allez sur https://myaccount.google.com/apppasswords
    // 3. Créez un nouveau mot de passe d'application pour "Autre (nom personnalisé)"
    // 4. Utilisez ce mot de passe généré ici
    private static final String EMAIL_FROM = "wajdimejbri631@gmail.com"; // Remplacez par votre email Gmail
    private static final String EMAIL_PASSWORD = "btmp ypbl fsds kvca"; // Remplacez par votre mot de passe d'application
    
    /**
     * Envoie un email
     * @param to Adresse email du destinataire
     * @param subject Sujet de l'email
     * @param content Contenu de l'email (peut contenir du HTML)
     * @return true si l'email a été envoyé avec succès, false sinon
     */
    public static boolean sendEmail(String to, String subject, String content) {
        // En mode simulation, on n'envoie pas réellement l'email
        if (SIMULATION_MODE) {
            return simulateEmailSending(to, subject, content);
        }
        
        // Vérifier si les informations d'authentification ont été configurées
        if (EMAIL_FROM.equals("your.email@gmail.com") || EMAIL_PASSWORD.equals("your-app-password")) {
            System.err.println("⚠️ ERREUR: Vous devez configurer votre email et mot de passe dans la classe EmailSender!");
            System.err.println("Pour Gmail, vous devez créer un mot de passe d'application spécifique:");
            System.err.println("1. Activez l'authentification à deux facteurs sur votre compte Google");
            System.err.println("2. Allez sur https://myaccount.google.com/apppasswords");
            System.err.println("3. Créez un nouveau mot de passe d'application pour \"Autre (nom personnalisé)\"");
            System.err.println("4. Utilisez ce mot de passe généré dans la classe EmailSender");
            return false;
        }
        
        // Configurer les propriétés pour la session
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", SMTP_HOST);
        props.put("mail.smtp.port", SMTP_PORT);
        props.put("mail.smtp.ssl.trust", SMTP_HOST);
        props.put("mail.smtp.ssl.protocols", "TLSv1.2");
        
        // Propriétés supplémentaires pour Gmail
        props.put("mail.smtp.socketFactory.port", SMTP_PORT);
        props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        props.put("mail.smtp.socketFactory.fallback", "false");
        
        // Activer le débogage pour voir les détails de la connexion SMTP
        props.put("mail.debug", "true");
        
        try {
            System.out.println("Tentative de connexion à " + SMTP_HOST + ":" + SMTP_PORT + " avec l'utilisateur " + EMAIL_FROM);
            
            // Créer une session avec authentification
            Session session = Session.getInstance(props, new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(EMAIL_FROM, EMAIL_PASSWORD);
                }
            });
            
            // Créer le message
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(EMAIL_FROM));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
            message.setSubject(subject);
            message.setContent(content, "text/html; charset=utf-8");
            
            // Envoyer le message
            Transport.send(message);
            
            System.out.println("Email envoyé avec succès à " + to);
            return true;
            
        } catch (MessagingException e) {
            System.err.println("Erreur lors de l'envoi de l'email: " + e.getMessage());
            e.printStackTrace();
            
            // Afficher des instructions spécifiques en fonction de l'erreur
            if (e instanceof AuthenticationFailedException) {
                System.err.println("\n⚠️ ERREUR D'AUTHENTIFICATION: Vos identifiants Gmail ne sont pas acceptés.");
                System.err.println("Assurez-vous que:");
                System.err.println("1. Vous avez activé l'authentification à deux facteurs sur votre compte Google");
                System.err.println("2. Vous utilisez un mot de passe d'application et non votre mot de passe Google normal");
                System.err.println("3. Le mot de passe d'application est correctement copié sans espaces supplémentaires");
                System.err.println("4. L'adresse email est correcte et correspond au compte qui a généré le mot de passe d'application");
                System.err.println("Pour créer un mot de passe d'application, visitez: https://myaccount.google.com/apppasswords");
            }
            
            return false;
        }
    }
    
    /**
     * Simule l'envoi d'un email en affichant son contenu dans la console et dans une boîte de dialogue
     * @param to Adresse email du destinataire
     * @param subject Sujet de l'email
     * @param content Contenu de l'email
     * @return true (toujours réussi en mode simulation)
     */
    private static boolean simulateEmailSending(String to, String subject, String content) {
        System.out.println("\n========== SIMULATION D'ENVOI D'EMAIL ==========");
        System.out.println("À: " + to);
        System.out.println("Sujet: " + subject);
        System.out.println("Contenu HTML: " + content);
        System.out.println("===============================================\n");
        
        // Afficher une boîte de dialogue avec le contenu de l'email (dans le thread JavaFX)
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Email Simulé");
            alert.setHeaderText("Un email a été simulé à: " + to);
            alert.setContentText("Sujet: " + subject);
            
            // Créer une zone de texte pour afficher le contenu HTML
            TextArea textArea = new TextArea(content);
            textArea.setEditable(false);
            textArea.setWrapText(true);
            textArea.setPrefHeight(300);
            
            GridPane gridPane = new GridPane();
            gridPane.setMaxWidth(Double.MAX_VALUE);
            gridPane.add(textArea, 0, 0);
            GridPane.setHgrow(textArea, Priority.ALWAYS);
            GridPane.setVgrow(textArea, Priority.ALWAYS);
            
            alert.getDialogPane().setExpandableContent(gridPane);
            alert.getDialogPane().setExpanded(true);
            
            alert.showAndWait();
        });
        
        return true;
    }
    
    /**
     * Génère un contenu HTML pour l'email de confirmation de réservation
     * @param nomFoyer Nom du foyer réservé
     * @param dateDebut Date de début de la réservation
     * @param dateFin Date de fin de la réservation
     * @param dateReservation Date de la réservation
     * @return Le contenu HTML de l'email
     */
    public static String generateReservationConfirmationEmail(String nomFoyer, String dateDebut, String dateFin, String dateReservation) {
        return "<!DOCTYPE html>"
                + "<html>"
                + "<head>"
                + "<style>"
                + "body { font-family: Arial, sans-serif; line-height: 1.6; }"
                + ".container { width: 600px; margin: 0 auto; padding: 20px; border: 1px solid #ddd; border-radius: 5px; }"
                + "h1 { color: #2196F3; }"
                + ".details { background-color: #f9f9f9; padding: 15px; border-radius: 5px; margin-top: 20px; }"
                + ".footer { margin-top: 30px; font-size: 12px; color: #777; }"
                + "</style>"
                + "</head>"
                + "<body>"
                + "<div class='container'>"
                + "<h1>Bienvenue dans notre foyer " + nomFoyer + " !</h1>"
                + "<p>Nous sommes ravis de vous accueillir et nous vous remercions pour votre réservation.</p>"
                + "<div class='details'>"
                + "<h3>Détails de votre réservation :</h3>"
                + "<p><strong>Foyer :</strong> " + nomFoyer + "</p>"
                + "<p><strong>Date de réservation :</strong> " + dateReservation + "</p>"
                + "<p><strong>Période de séjour :</strong> Du " + dateDebut + " au " + dateFin + "</p>"
                + "</div>"
                + "<p>Nous espérons que votre séjour sera agréable. N'hésitez pas à nous contacter si vous avez des questions.</p>"
                + "<div class='footer'>"
                + "<p>Ceci est un email automatique, merci de ne pas y répondre.</p>"
                + "</div>"
                + "</div>"
                + "</body>"
                + "</html>";
    }
}
