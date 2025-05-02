package controllers;

import Services.ServiceReservation;
import entities.Reservation;
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
import java.sql.SQLException;

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
    private ServiceReservation serviceReservation;
    
    @FXML
    public void initialize() {
        serviceReservation = new ServiceReservation();
        
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
            // Créer une nouvelle réservation
            Reservation reservation = new Reservation();
            reservation.setIdRestaurant(restaurant.getIdRestaurant());
            reservation.setNomClient(nomClientField.getText().trim());
            reservation.setEmailClient(emailField.getText().trim());
            reservation.setNombrePersonnes(personnesSpinner.getValue());
            reservation.setStatut("En attente"); // Statut par défaut
            
            // Enregistrer la réservation
            serviceReservation.ajouter(reservation);
            
            // Afficher un message de succès
            showAlert("Réservation confirmée", 
                     "Votre réservation chez " + restaurant.getNom() + " a été enregistrée avec succès.\n" +
                     "Nombre de personnes: " + personnesSpinner.getValue(),
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
}
