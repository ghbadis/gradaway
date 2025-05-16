package utils;

import entities.ReservationVol;
import entities.Vols;
import javax.mail.*;
import javax.mail.internet.*;

import java.time.format.DateTimeFormatter;
import java.util.Properties;

/**
 * Envoie un email de confirmation pour une réservation de vol
 *
 * //@param toEmail Email du destinataire
 * //@param reservation Objet de réservation
 * //@param vol Objet vol associé à la réservation
 * @return true si l'email a été envoyé avec succès, false sinon
 */
public class EmailUtils {
    // Configuration pour l'envoi d'emails
    private static final String EMAIL_HOST = "smtp.gmail.com";
    private static final String EMAIL_PORT = "587";
    private static final String EMAIL_USERNAME = "awaygrad@gmail.com"; // Votre email Gmail
    private static final String EMAIL_PASSWORD = "dmzq fnaw uglm glyn"; // Votre mot de passe d'application

    /**
     * Envoie un email de confirmation pour une réservation de vol
     *
     * @param toEmail Email du destinataire
     * @param reservation Objet de réservation
     * @param vol Objet vol associé à la réservation
     * @return true si l'email a été envoyé avec succès, false sinon
     */
    public static boolean sendReservationConfirmation(String toEmail, ReservationVol reservation, Vols vol) {
        try {
            // Configurer les propriétés pour la connexion SMTP
            Properties props = new Properties();
            props.put("mail.smtp.auth", "true");
            props.put("mail.smtp.starttls.enable", "true");
            props.put("mail.smtp.host", EMAIL_HOST);
            props.put("mail.smtp.port", EMAIL_PORT);

            // Désactiver la validation stricte des types MIME
            props.put("mail.mime.address.strict", "false");

            // Créer une session avec authentification
            Session session = Session.getInstance(props, new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(EMAIL_USERNAME, EMAIL_PASSWORD);
                }
            });

            // Activer le débogage pour voir les messages d'erreur détaillés
            session.setDebug(true);

            // Créer le message
            MimeMessage message = new MimeMessage(session);
            message.setFrom(new InternetAddress(EMAIL_USERNAME));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail));
            message.setSubject("Confirmation de votre réservation de vol - " + reservation.getReferenceReservation());

            // Formater la date et l'heure pour l'affichage
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
            String dateReservation = reservation.getDateReservation().format(formatter);
            String dateDepart = vol.getDateDepart().format(formatter);
            String dateArrivee = vol.getDateArrivee().format(formatter);

            // Créer le contenu HTML de l'email
            String htmlContent =
                    "<html>" +
                            "<head>" +
                            "<style>" +
                            "body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; }" +
                            ".container { width: 600px; margin: 0 auto; padding: 20px; }" +
                            ".header { background-color: #4CAF50; color: white; padding: 10px; text-align: center; }" +
                            ".content { padding: 20px; border: 1px solid #ddd; }" +
                            ".footer { background-color: #f1f1f1; padding: 10px; text-align: center; font-size: 12px; }" +
                            "table { width: 100%; border-collapse: collapse; margin-bottom: 20px; }" +
                            "th, td { padding: 10px; text-align: left; border-bottom: 1px solid #ddd; }" +
                            "th { background-color: #f2f2f2; }" +
                            ".important { color: #4CAF50; font-weight: bold; }" +
                            "</style>" +
                            "</head>" +
                            "<body>" +
                            "<div class='container'>" +
                            "<div class='header'>" +
                            "<h2>Confirmation de Réservation</h2>" +
                            "</div>" +
                            "<div class='content'>" +
                            "<p>Cher(e) client(e),</p>" +
                            "<p>Nous vous remercions pour votre réservation. Voici les détails de votre vol :</p>" +
                            "<table>" +
                            "<tr><th colspan='2'>Informations de réservation</th></tr>" +
                            "<tr><td>Référence :</td><td class='important'>" + reservation.getReferenceReservation() + "</td></tr>" +
                            "<tr><td>Date de réservation :</td><td>" + dateReservation + "</td></tr>" +
                            "<tr><td>Statut :</td><td>" + reservation.getStatutPaiement() + "</td></tr>" +
                            "<tr><th colspan='2'>Détails du vol</th></tr>" +
                            "<tr><td>Compagnie :</td><td>" + vol.getCompagnie() + "</td></tr>" +
                            "<tr><td>Numéro de vol :</td><td>" + vol.getNumeroVol() + "</td></tr>" +
                            "<tr><td>Départ :</td><td>" + vol.getVilleDepart() + " (" + vol.getAeroportDepart() + ")</td></tr>" +
                            "<tr><td>Arrivée :</td><td>" + vol.getVilleArrivee() + " (" + vol.getAeroportArrivee() + ")</td></tr>" +
                            "<tr><td>Date de départ :</td><td>" + dateDepart + "</td></tr>" +
                            "<tr><td>Date d'arrivée :</td><td>" + dateArrivee + "</td></tr>" +
                            "<tr><th colspan='2'>Votre réservation</th></tr>" +
                            "<tr><td>Nombre de places :</td><td>" + reservation.getNombrePlaces() + "</td></tr>" +
                            "<tr><td>Classe :</td><td>" + reservation.getClasse() + "</td></tr>" +
                            "<tr><td>Type de bagage :</td><td>" + reservation.getTypeBagage() + "</td></tr>" +
                            "<tr><td>Prix total :</td><td class='important'>" + String.format("%.2f €", reservation.getPrixTotal()) + "</td></tr>" +
                            "</table>" +
                            "<p>Nous vous souhaitons un excellent voyage !</p>" +
                            "<p>Pour toute question concernant votre réservation, veuillez nous contacter en mentionnant votre référence.</p>" +
                            "</div>" +
                            "<div class='footer'>" +
                            "<p>Ceci est un email automatique, merci de ne pas y répondre.</p>" +
                            "</div>" +
                            "</div>" +
                            "</body>" +
                            "</html>";

            // Utiliser une approche alternative pour définir le contenu HTML
            // Créer une partie multipart
            Multipart multipart = new MimeMultipart("alternative");

            // Créer une partie pour le contenu HTML
            MimeBodyPart htmlPart = new MimeBodyPart();
            htmlPart.setContent(htmlContent, "text/html; charset=utf-8");

            // Ajouter la partie HTML au multipart
            multipart.addBodyPart(htmlPart);

            // Définir le contenu du message comme multipart
            message.setContent(multipart);

            // Enregistrer les changements
            message.saveChanges();

            // Envoyer le message
            Transport.send(message);

            System.out.println("Email de confirmation envoyé avec succès à " + toEmail);
            return true;

        } catch (MessagingException e) {
            System.err.println("Erreur lors de l'envoi de l'email: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
}