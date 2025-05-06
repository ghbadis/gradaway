package utils;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import java.io.IOException;

/**
 * Classe utilitaire pour l'envoi d'emails via une API simple
 * Cette implémentation utilise l'API gratuite EmailJS pour envoyer des emails
 */
public class SimpleEmailSender {

    // Configuration de l'API EmailJS
    private static final String EMAILJS_URL = "https://api.emailjs.com/api/v1.0/email/send";
    private static final String EMAILJS_SERVICE_ID = "service_c9hc0qr"; // Remplacez par votre service ID
    private static final String EMAILJS_TEMPLATE_ID = "template_5uxnl0n"; // Remplacez par votre template ID
    private static final String EMAILJS_USER_ID = "Yl7Hl3jRnRKxMbLXm"; // Remplacez par votre user ID
    
    /**
     * Envoie un email via l'API EmailJS
     * @param to Adresse email du destinataire
     * @param subject Sujet de l'email
     * @param content Contenu de l'email (peut être du HTML)
     * @return true si l'envoi a réussi, false sinon
     */
    public static boolean sendEmail(String to, String subject, String content) {
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            // Créer la requête POST
            HttpPost httpPost = new HttpPost(EMAILJS_URL);
            httpPost.setHeader("Content-Type", "application/json");
            
            // Créer le corps de la requête JSON
            JSONObject templateParams = new JSONObject();
            templateParams.put("to_email", to);
            templateParams.put("subject", subject);
            templateParams.put("message", content);
            
            JSONObject requestBody = new JSONObject();
            requestBody.put("service_id", EMAILJS_SERVICE_ID);
            requestBody.put("template_id", EMAILJS_TEMPLATE_ID);
            requestBody.put("user_id", EMAILJS_USER_ID);
            requestBody.put("template_params", templateParams);
            
            // Définir le corps de la requête
            StringEntity stringEntity = new StringEntity(requestBody.toString());
            httpPost.setEntity(stringEntity);
            
            // Exécuter la requête
            System.out.println("Envoi d'un email à " + to + " via EmailJS...");
            try (CloseableHttpResponse response = httpClient.execute(httpPost)) {
                int statusCode = response.getStatusLine().getStatusCode();
                HttpEntity entity = response.getEntity();
                String responseBody = entity != null ? EntityUtils.toString(entity) : null;
                
                System.out.println("Réponse de l'API EmailJS: " + statusCode);
                if (responseBody != null) {
                    System.out.println("Corps de la réponse: " + responseBody);
                }
                
                // Vérifier si l'envoi a réussi
                boolean success = statusCode >= 200 && statusCode < 300;
                if (success) {
                    System.out.println("Email envoyé avec succès à " + to);
                } else {
                    System.err.println("Échec de l'envoi de l'email à " + to + ". Code: " + statusCode);
                }
                
                return success;
            }
        } catch (IOException e) {
            System.err.println("Erreur lors de l'envoi de l'email: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
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
        return "Bienvenue dans notre foyer " + nomFoyer + " !\n\n" +
               "Nous sommes ravis de vous accueillir et nous vous remercions pour votre réservation.\n\n" +
               "Détails de votre réservation :\n" +
               "Foyer : " + nomFoyer + "\n" +
               "Date de réservation : " + dateReservation + "\n" +
               "Période de séjour : Du " + dateDebut + " au " + dateFin + "\n\n" +
               "Informations importantes :\n" +
               "- Veuillez vous présenter à l'accueil du foyer avec votre carte d'étudiant le jour de votre arrivée.\n" +
               "- Les horaires d'accueil sont de 8h à 20h du lundi au vendredi, et de 10h à 18h le weekend.\n" +
               "- Un dépôt de garantie de 200€ vous sera demandé à votre arrivée.\n" +
               "- Le règlement intérieur du foyer vous sera remis à votre arrivée.\n\n" +
               "Nous espérons que votre séjour sera agréable. N'hésitez pas à nous contacter si vous avez des questions.\n\n" +
               "Cordialement,\n" +
               "L'équipe de gestion du foyer " + nomFoyer + "\n\n" +
               "Ceci est un email automatique, merci de ne pas y répondre.";
    }
}
