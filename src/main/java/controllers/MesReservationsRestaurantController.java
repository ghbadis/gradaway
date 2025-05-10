package controllers;

import Services.ServiceReservationRestaurant;
import Services.ServiceRestaurant;
import entities.ReservationRestaurant;
import entities.Restaurant;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.animation.FadeTransition;
import javafx.util.Duration;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class MesReservationsRestaurantController {

    @FXML private TilePane reservationsContainer;
    
    private ServiceReservationRestaurant serviceReservation;
    private ServiceRestaurant serviceRestaurant;
    private int userId; // ID de l'étudiant connecté
    
    @FXML
    public void initialize() {
        serviceReservation = new ServiceReservationRestaurant();
        serviceRestaurant = new ServiceRestaurant();
        
        // Par défaut, on utilise l'ID 2 qui semble avoir des réservations
        userId = 2;
        
        // Les réservations seront chargées après l'initialisation du userId
        loadReservations();
    }
    
    /**
     * Définit l'ID de l'utilisateur et charge ses réservations
     * @param userId ID de l'utilisateur
     */
    public void setUserId(int userId) {
        this.userId = userId;
        
        // Recharger les réservations avec le nouvel ID
        reservationsContainer.getChildren().clear();
        loadReservations();
    }
    
    /**
     * Charge toutes les réservations de restaurant
     */
    private void loadReservations() {
        try {
            System.out.println("Chargement de toutes les réservations de restaurant");
            
            // Récupérer toutes les réservations sans filtrer par ID d'utilisateur
            List<ReservationRestaurant> reservations = serviceReservation.getAllReservations();
            
            System.out.println("Nombre total de réservations trouvées: " + reservations.size());
            
            if (reservations.isEmpty()) {
                System.out.println("Aucune réservation trouvée, affichage du message 'pas de réservations'");
                showNoReservationsMessage();
                return;
            }
            
            // Configurer le TilePane
            reservationsContainer.setPrefColumns(3);
            reservationsContainer.setPrefTileWidth(280);
            reservationsContainer.setPrefTileHeight(320);
            reservationsContainer.setHgap(15);
            reservationsContainer.setVgap(15);
            reservationsContainer.setAlignment(Pos.CENTER);
            reservationsContainer.setTileAlignment(Pos.CENTER);
            
            // Calculer le nombre de lignes nécessaires
            int numRows = (int) Math.ceil(reservations.size() / 3.0);
            reservationsContainer.setPrefRows(numRows);
            reservationsContainer.setPrefHeight(numRows * (320 + 15) + 15);
            
            // Afficher chaque réservation
            for (ReservationRestaurant reservation : reservations) {
                try {
                    // Récupérer le restaurant associé à la réservation
                    Restaurant restaurant = serviceRestaurant.recupererParId(reservation.getIdRestaurant());
                    
                    // Créer la carte de réservation
                    VBox reservationCard = createReservationCard(reservation, restaurant);
                    reservationsContainer.getChildren().add(reservationCard);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        } catch (SQLException e) {
            showAlert("Erreur", "Erreur lors du chargement des réservations: " + e.getMessage(), Alert.AlertType.ERROR);
            e.printStackTrace();
        }
    }
    
    /**
     * Affiche un message quand il n'y a pas de réservations
     */
    private void showNoReservationsMessage() {
        Label noReservationsLabel = new Label("Vous n'avez pas encore de réservations.");
        noReservationsLabel.setStyle("-fx-font-size: 18px; -fx-text-fill: #757575;");
        reservationsContainer.getChildren().add(noReservationsLabel);
    }
    
    /**
     * Crée une carte pour afficher une réservation
     */
    private VBox createReservationCard(ReservationRestaurant reservation, Restaurant restaurant) {
        // Conteneur principal
        VBox card = new VBox();
        card.setStyle("-fx-background-color: white; -fx-background-radius: 8px; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 10, 0, 0, 3);");
        card.setPrefHeight(320);
        card.setMinHeight(320);
        card.setMaxHeight(320);
        card.setPrefWidth(280);
        card.setMinWidth(280);
        card.setMaxWidth(280);
        card.setSpacing(10);
        card.setPadding(new Insets(10));
        card.setAlignment(Pos.TOP_CENTER);
        
        // Image du restaurant
        ImageView imageView = new ImageView();
        imageView.setFitHeight(150);
        imageView.setFitWidth(260);
        imageView.setPreserveRatio(true);
        
        // Charger l'image
        try {
            if (restaurant.getImage() != null && !restaurant.getImage().isEmpty()) {
                String imagePath = "src/main/resources/" + restaurant.getImage();
                File file = new File(imagePath);
                if (file.exists()) {
                    Image image = new Image(file.toURI().toString());
                    imageView.setImage(image);
                } else {
                    // Image par défaut si l'image n'existe pas
                    Image defaultImage = new Image(getClass().getResourceAsStream("/images/placeholder-restaurant.png"));
                    imageView.setImage(defaultImage);
                }
            } else {
                // Image par défaut si pas d'image
                Image defaultImage = new Image(getClass().getResourceAsStream("/images/placeholder-restaurant.png"));
                imageView.setImage(defaultImage);
            }
        } catch (Exception e) {
            e.printStackTrace();
            // Image par défaut en cas d'erreur
            try {
                Image defaultImage = new Image(getClass().getResourceAsStream("/images/placeholder-restaurant.png"));
                imageView.setImage(defaultImage);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        
        // Conteneur pour les informations
        VBox infoContainer = new VBox();
        infoContainer.setSpacing(5);
        infoContainer.setPrefWidth(260);
        infoContainer.setAlignment(Pos.CENTER_LEFT);
        
        // Nom du restaurant
        Label nameLabel = new Label(restaurant.getNom());
        nameLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #2196F3;");
        nameLabel.setWrapText(true);
        
        // Date de réservation
        String dateFormatted = reservation.getDateReservation().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        Label dateLabel = new Label("Date: " + dateFormatted);
        dateLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #333333;");
        dateLabel.setWrapText(true);
        
        // Nombre de personnes
        Label personnesLabel = new Label("Personnes: " + reservation.getNombrePersonnes());
        personnesLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #333333;");
        personnesLabel.setWrapText(true);
        
        // Statut de la réservation (on pourrait ajouter un statut dans l'entité ReservationRestaurant)
        Label statusLabel = new Label("Statut: Confirmé");
        statusLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #4CAF50; -fx-font-weight: bold;");
        statusLabel.setWrapText(true);
        
        // Bouton Annuler
        Button cancelButton = new Button("Annuler la réservation");
        cancelButton.setStyle("-fx-background-color: #F44336; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 20px;");
        cancelButton.setPrefWidth(200);
        cancelButton.setPrefHeight(30);
        cancelButton.setOnAction(e -> cancelReservation(reservation));
        
        // Ajouter les éléments au conteneur d'informations
        infoContainer.getChildren().addAll(nameLabel, dateLabel, personnesLabel, statusLabel);
        
        // Ajouter un espace flexible pour pousser le bouton vers le bas
        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);
        infoContainer.getChildren().add(spacer);
        
        // Ajouter les éléments à la carte
        card.getChildren().addAll(imageView, infoContainer, cancelButton);
        
        return card;
    }
    
    /**
     * Annule une réservation
     */
    private void cancelReservation(ReservationRestaurant reservation) {
        try {
            // Supprimer la réservation
            serviceReservation.supprimer(reservation);
            
            // Recharger les réservations
            reservationsContainer.getChildren().clear();
            loadReservations();
            
            showAlert("Succès", "Votre réservation a été annulée avec succès.", Alert.AlertType.INFORMATION);
        } catch (SQLException e) {
            showAlert("Erreur", "Erreur lors de l'annulation de la réservation: " + e.getMessage(), Alert.AlertType.ERROR);
            e.printStackTrace();
        }
    }
    
    /**
     * Retourne à l'accueil
     */
    @FXML
    public void retourAccueil() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Accueil.fxml"));
            Parent root = loader.load();
            
            Scene scene = new Scene(root);
            Stage stage = (Stage) reservationsContainer.getScene().getWindow();
            
            // Transition de fondu
            FadeTransition fadeIn = new FadeTransition(Duration.millis(300), root);
            fadeIn.setFromValue(0.0);
            fadeIn.setToValue(1.0);
            
            stage.setScene(scene);
            fadeIn.play();
        } catch (IOException e) {
            showAlert("Erreur", "Erreur lors du chargement de la page d'accueil: " + e.getMessage(), Alert.AlertType.ERROR);
            e.printStackTrace();
        }
    }
    
    /**
     * Retourne à la liste des restaurants
     */
    @FXML
    private void retourListeRestaurant() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ListRestaurantClient.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) reservationsContainer.getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);

            // Transition de fondu
            FadeTransition fadeIn = new FadeTransition(Duration.millis(300), root);
            fadeIn.setFromValue(0.0);
            fadeIn.setToValue(1.0);
            fadeIn.play();

        } catch (IOException e) {
            e.printStackTrace();
            // Afficher une alerte en cas d'erreur
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur");
            alert.setHeaderText(null);
            alert.setContentText("Erreur lors de la navigation: " + e.getMessage());
            alert.showAndWait();
        }
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
