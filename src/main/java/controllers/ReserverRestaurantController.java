package controllers;

import Services.ServiceReservationRestaurant;
import entities.ReservationRestaurant;
import entities.Restaurant;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import javafx.animation.FadeTransition;
import javafx.util.Duration;

import java.io.File;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;

public class ReserverRestaurantController {

    @FXML private Label headerLabel;
    @FXML private ImageView restaurantImage;
    @FXML private Label nomLabel;
    @FXML private Label adresseLabel;
    @FXML private Label villeLabel;
    @FXML private Label capaciteLabel;
    @FXML private Label horairesLabel;
    
    @FXML private TextField nomClientField;
    @FXML private TextField emailField;
    @FXML private Spinner<Integer> personnesSpinner;
    
    @FXML private Button reserverButton;
    @FXML private Button annulerButton;
    
    private Restaurant restaurant;
    private ServiceReservationRestaurant serviceReservation;
    
    @FXML
    public void initialize() {
        serviceReservation = new ServiceReservationRestaurant();
        
        // Configuration du spinner pour le nombre de personnes (1-20 par défaut)
        SpinnerValueFactory<Integer> valueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 20, 2);
        personnesSpinner.setValueFactory(valueFactory);
    }
    
    /**
     * Définit le restaurant à réserver et met à jour l'interface
     */
    public void setRestaurant(Restaurant restaurant) {
        this.restaurant = restaurant;
        
        if (restaurant != null) {
            // Mettre à jour l'en-tête
            headerLabel.setText("Réserver chez " + restaurant.getNom());
            
            // Mettre à jour les informations du restaurant
            nomLabel.setText(restaurant.getNom());
            adresseLabel.setText(restaurant.getAdresse());
            villeLabel.setText(restaurant.getVille() + ", " + restaurant.getPays());
            capaciteLabel.setText(String.valueOf(restaurant.getCapaciteTotale()) + " personnes");
            horairesLabel.setText(restaurant.getHoraireOuverture() + " - " + restaurant.getHoraireFermeture());
            
            // Charger l'image du restaurant
            try {
                if (restaurant.getImage() != null && !restaurant.getImage().isEmpty()) {
                    String imagePath = "src/main/resources/" + restaurant.getImage();
                    File file = new File(imagePath);
                    if (file.exists()) {
                        Image image = new Image(file.toURI().toString());
                        restaurantImage.setImage(image);
                    } else {
                        // Image par défaut si l'image n'existe pas
                        Image defaultImage = new Image(getClass().getResourceAsStream("/images/placeholder-restaurant.png"));
                        restaurantImage.setImage(defaultImage);
                    }
                } else {
                    // Image par défaut si pas d'image
                    Image defaultImage = new Image(getClass().getResourceAsStream("/images/placeholder-restaurant.png"));
                    restaurantImage.setImage(defaultImage);
                }
            } catch (Exception e) {
                e.printStackTrace();
                // Image par défaut en cas d'erreur
                try {
                    Image defaultImage = new Image(getClass().getResourceAsStream("/images/placeholder-restaurant.png"));
                    restaurantImage.setImage(defaultImage);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
            
            // Mettre à jour le spinner pour le nombre de personnes (max = capacité du restaurant)
            int maxPersonnes = Math.min(restaurant.getCapaciteTotale(), 50); // Limiter à 50 pour l'interface
            SpinnerValueFactory<Integer> valueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(1, maxPersonnes, 2);
            personnesSpinner.setValueFactory(valueFactory);
        }
    }
    
    /**
     * Gère la réservation du restaurant
     */
    @FXML
    void reserverRestaurant(ActionEvent event) {
        if (!validateFields()) {
            return;
        }
        
        try {
            // Récupérer l'email saisi
            String email = emailField.getText().trim();
            
            // Vérifier si l'utilisateur existe dans la base de données
            int idEtudiantValue = getUserIdByEmail(email);
            
            // Si l'utilisateur n'existe pas, créer un nouvel utilisateur
            if (idEtudiantValue == -1) {
                idEtudiantValue = createNewUser(nomClientField.getText().trim(), email);
                
                // Si la création a échoué, afficher une erreur et arrêter
                if (idEtudiantValue == -1) {
                    showAlert("Erreur", "Impossible de créer un compte utilisateur. Veuillez réessayer.", Alert.AlertType.ERROR);
                    return;
                }
            }
            
            // Créer une nouvelle réservation avec les valeurs
            int idRestaurantValue = restaurant.getIdRestaurant();
            LocalDate dateReservationValue = LocalDate.now();
            int nombrePersonnesValue = personnesSpinner.getValue();
            
            // Créer l'objet ReservationRestaurant avec le constructeur approprié
            ReservationRestaurant reservation = new ReservationRestaurant(
                idRestaurantValue, 
                idEtudiantValue, 
                dateReservationValue, 
                nombrePersonnesValue
            );
            
            // Enregistrer la réservation
            serviceReservation.ajouter(reservation);
            
            // Envoyer un email de confirmation
            sendConfirmationEmail(email, restaurant.getNom(), dateReservationValue, nombrePersonnesValue);
            
            // Afficher un message de succès
            showAlert("Réservation confirmée", 
                     "Votre réservation chez " + restaurant.getNom() + " a été enregistrée avec succès.\n" +
                     "Nombre de personnes: " + nombrePersonnesValue + "\n\n" +
                     "Un email de confirmation a été envoyé à " + email,
                     Alert.AlertType.INFORMATION);
            
            // Retourner à la liste des restaurants
            retournerALaListe();
            
        } catch (SQLException e) {
            showAlert("Erreur", "Erreur lors de l'enregistrement de la réservation: " + e.getMessage(), Alert.AlertType.ERROR);
            e.printStackTrace();
        } catch (Exception e) {
            showAlert("Erreur", "Une erreur inattendue est survenue: " + e.getMessage(), Alert.AlertType.ERROR);
            e.printStackTrace();
        }
    }
    
    /**
     * Annule la réservation et retourne à la liste des restaurants
     */
    @FXML
    void annulerReservation(ActionEvent event) {
        retournerALaListe();
    }
    
    /**
     * Retourne à la liste des restaurants
     */
    private void retournerALaListe() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/ListRestaurantClient.fxml"));
            Scene scene = new Scene(root);
            Stage stage = (Stage) reserverButton.getScene().getWindow();
            
            // Transition de fondu
            FadeTransition fadeIn = new FadeTransition(Duration.millis(300), root);
            fadeIn.setFromValue(0.0);
            fadeIn.setToValue(1.0);
            
            stage.setScene(scene);
            fadeIn.play();
        } catch (IOException e) {
            showAlert("Erreur", "Erreur lors du chargement de la vue: " + e.getMessage(), Alert.AlertType.ERROR);
            e.printStackTrace();
        }
    }
    
    /**
     * Valide les champs du formulaire
     */
    private boolean validateFields() {
        StringBuilder errors = new StringBuilder();
        
        if (nomClientField.getText().trim().isEmpty()) {
            errors.append("- Le nom est requis\n");
        }
        
        if (emailField.getText().trim().isEmpty()) {
            errors.append("- L'email est requis\n");
        } else if (!isValidEmail(emailField.getText().trim())) {
            errors.append("- Format d'email invalide\n");
        }
        
        if (errors.length() > 0) {
            showAlert("Validation échouée", "Veuillez corriger les erreurs suivantes:\n" + errors.toString(), Alert.AlertType.ERROR);
            return false;
        }
        
        return true;
    }
    
    /**
     * Vérifie si l'email est valide
     */
    private boolean isValidEmail(String email) {
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
        return email.matches(emailRegex);
    }
    
    /**
     * Affiche une alerte
     */
    private void showAlert(String title, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    /**
     * Récupère l'ID d'un utilisateur à partir de son email
     * @param email Email de l'utilisateur
     * @return ID de l'utilisateur ou -1 si non trouvé
     */
    private int getUserIdByEmail(String email) {
        try {
            String query = "SELECT id FROM user WHERE email = ?";
            PreparedStatement ps = serviceReservation.getCon().prepareStatement(query);
            ps.setString(1, email);
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                return rs.getInt("id");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1; // Utilisateur non trouvé
    }
    
    /**
     * Crée un nouvel utilisateur dans la base de données
     * @param nom Nom de l'utilisateur
     * @param email Email de l'utilisateur
     * @return ID du nouvel utilisateur ou -1 en cas d'échec
     */
    private int createNewUser(String nom, String email) {
        try {
            // Générer un mot de passe temporaire (à remplacer par un système plus sécurisé)
            String tempPassword = "temp" + System.currentTimeMillis() % 10000;
            
            // Insérer le nouvel utilisateur
            String query = "INSERT INTO user (nom, email, password, role) VALUES (?, ?, ?, 'ETUDIANT')";
            PreparedStatement ps = serviceReservation.getCon().prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, nom);
            ps.setString(2, email);
            ps.setString(3, tempPassword);
            ps.executeUpdate();
            
            // Récupérer l'ID généré
            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1; // Échec de la création
    }
    
    /**
     * Envoie un email de confirmation de réservation
     * @param email Email du destinataire
     * @param nomRestaurant Nom du restaurant
     * @param dateReservation Date de la réservation
     * @param nombrePersonnes Nombre de personnes
     */
    private void sendConfirmationEmail(String email, String nomRestaurant, LocalDate dateReservation, int nombrePersonnes) {
        try {
            // Formater la date pour l'affichage
            String dateFormatted = dateReservation.format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy"));
            
            // Générer le contenu HTML de l'email
            String htmlContent = generateReservationConfirmationEmail(nomRestaurant, dateFormatted, nombrePersonnes);
            
            // Envoyer l'email
            utils.EmailSender.sendEmail(
                email, 
                "Confirmation de réservation - " + nomRestaurant, 
                htmlContent
            );
            
            System.out.println("Email de confirmation envoyé à " + email);
        } catch (Exception e) {
            System.err.println("Erreur lors de l'envoi de l'email: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Génère le contenu HTML pour l'email de confirmation de réservation
     */
    private String generateReservationConfirmationEmail(String nomRestaurant, String dateReservation, int nombrePersonnes) {
        return "<!DOCTYPE html>" +
             "<html>" +
             "<head>" +
             "    <meta charset='UTF-8'>" +
             "    <style>" +
             "        body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; }" +
             "        .container { max-width: 600px; margin: 0 auto; padding: 20px; }" +
             "        .header { background-color: #4CAF50; color: white; padding: 10px; text-align: center; }" +
             "        .content { padding: 20px; border: 1px solid #ddd; }" +
             "        .footer { text-align: center; margin-top: 20px; font-size: 12px; color: #777; }" +
             "        table { width: 100%; border-collapse: collapse; margin: 20px 0; }" +
             "        th, td { padding: 10px; text-align: left; border-bottom: 1px solid #ddd; }" +
             "        th { background-color: #f2f2f2; }" +
             "    </style>" +
             "</head>" +
             "<body>" +
             "    <div class='container'>" +
             "        <div class='header'>" +
             "            <h1>Confirmation de Réservation</h1>" +
             "        </div>" +
             "        <div class='content'>" +
             "            <p>Cher(e) client(e),</p>" +
             "            <p>Nous sommes heureux de vous confirmer que votre réservation au restaurant a été confirmée.</p>" +
             "            <h2>Détails de la réservation :</h2>" +
             "            <table>" +
             "                <tr><th>Restaurant</th><td>" + nomRestaurant + "</td></tr>" +
             "                <tr><th>Date</th><td>" + dateReservation + "</td></tr>" +
             "                <tr><th>Nombre de personnes</th><td>" + nombrePersonnes + "</td></tr>" +
             "            </table>" +
             "            <p>Nous vous remercions pour votre réservation et sommes impatients de vous accueillir.</p>" +
             "            <p>Cordialement,<br>L'équipe du restaurant</p>" +
             "        </div>" +
             "        <div class='footer'>" +
             "            <p>Ceci est un email automatique, merci de ne pas y répondre.</p>" +
             "        </div>" +
             "    </div>" +
             "</body>" +
             "</html>";
    }
}
