package controllers;

import Services.ServiceReservationFoyer;
import Services.ServiceFoyer;
import entities.ReservationFoyer;
import entities.Foyer;
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
import java.io.InputStream;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class MesReservationsFoyerController {

    @FXML private TilePane reservationsContainer;
    
    private ServiceReservationFoyer serviceReservation;
    private ServiceFoyer serviceFoyer;
    private int userId;
    
    @FXML
    public void initialize() {
        serviceReservation = new ServiceReservationFoyer();
        serviceFoyer = new ServiceFoyer();
        
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
     * Charge toutes les réservations de foyer
     */
    private void loadReservations() {
        try {
            System.out.println("Chargement de toutes les réservations de foyer");
            
            // Récupérer toutes les réservations sans filtrer par ID d'utilisateur
            List<ReservationFoyer> reservations = serviceReservation.getAllReservations();
            
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
            for (ReservationFoyer reservation : reservations) {
                try {
                    // Récupérer le foyer associé à la réservation
                    Foyer foyer = serviceFoyer.getFoyerById(reservation.getFoyerId());
                    
                    // Créer la carte de réservation
                    VBox reservationCard = createReservationCard(reservation, foyer);
                    reservationsContainer.getChildren().add(reservationCard);
                } catch (SQLException e) {
                    System.out.println("Erreur lors de la récupération du foyer: " + e.getMessage());
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
    private VBox createReservationCard(ReservationFoyer reservation, Foyer foyer) {
        VBox card = new VBox();
        card.setStyle("-fx-background-color: white; -fx-background-radius: 8px; "
                + "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 10, 0, 0, 3);");
        card.setPrefHeight(320);
        card.setPrefWidth(280);
        card.setSpacing(10);
        card.setPadding(new Insets(10));
        card.setAlignment(Pos.TOP_CENTER);

        ImageView imageView = new ImageView();
        imageView.setFitHeight(150);
        imageView.setFitWidth(260);
        imageView.setPreserveRatio(true);

        try {
            Image image;
            String imageName = foyer.getImage();
            if (imageName != null && imageName.contains("/")) {
                imageName = imageName.substring(imageName.lastIndexOf('/') + 1);
            }
            if (imageName != null && !imageName.isEmpty()) {
                String imagePath = "/iamge/" + imageName;
                System.out.println("Tentative de chargement de l'image: " + imagePath);
                InputStream imageStream = getClass().getResourceAsStream(imagePath);
                if (imageStream != null) {
                    image = new Image(imageStream);
                    System.out.println("Image chargée avec succès: " + imagePath);
                } else {
                    System.out.println("Image introuvable dans le chemin: " + imagePath);
                    System.out.println("Tentative de chargement de l'image placeholder");
                    image = new Image(getClass().getResourceAsStream("/iamge/placeholder-foyer.png"));
                }
            } else {
                System.out.println("Aucune image spécifiée pour le foyer, utilisation du placeholder");
                image = new Image(getClass().getResourceAsStream("/iamge/placeholder-foyer.png"));
            }
            imageView.setImage(image);
        } catch (Exception e) {
            System.out.println("Erreur image: " + e.getMessage());
            try {
                imageView.setImage(new Image(getClass().getResourceAsStream("/image/placeholder-foyer.png")));
            } catch (Exception ex) {
                System.out.println("Erreur fallback image: " + ex.getMessage());
            }
        }

        Label nomLabel = new Label(foyer.getNom());
        nomLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        Label dateLabel = new Label("Du " + reservation.getDateDebut().format(formatter)
                + " au " + reservation.getDateFin().format(formatter));
        dateLabel.setStyle("-fx-font-size: 14px;");

        Label adresseLabel = new Label(foyer.getAdresse() + ", " + foyer.getVille());
        adresseLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #757575;");

        Button cancelButton = new Button("Annuler la réservation");
        cancelButton.setStyle("-fx-background-color: #FF5722; -fx-text-fill: white; -fx-background-radius: 4px;");
        cancelButton.setPrefWidth(200);
        cancelButton.setOnAction(e -> cancelReservation(reservation));

        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);

        card.getChildren().addAll(imageView, nomLabel, dateLabel, adresseLabel, spacer, cancelButton);

        card.setOnMouseEntered(e -> {
            FadeTransition ft = new FadeTransition(Duration.millis(100), card);
            ft.setFromValue(1.0);
            ft.setToValue(0.9);
            ft.play();
            card.setStyle("-fx-background-color: white; -fx-background-radius: 8px; "
                    + "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.2), 15, 0, 0, 5);");
        });

        card.setOnMouseExited(e -> {
            FadeTransition ft = new FadeTransition(Duration.millis(100), card);
            ft.setFromValue(0.9);
            ft.setToValue(1.0);
            ft.play();
            card.setStyle("-fx-background-color: white; -fx-background-radius: 8px; "
                    + "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 10, 0, 0, 3);");
        });

        return card;
    }

    /**
     * Annule une réservation
     */
    private void cancelReservation(ReservationFoyer reservation) {
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
            Parent root = FXMLLoader.load(getClass().getResource("/Accueil.fxml"));
            Scene scene = new Scene(root);
            Stage stage = (Stage) reservationsContainer.getScene().getWindow();
            
            // Transition de fondu
            FadeTransition fadeIn = new FadeTransition(Duration.millis(300), root);
            fadeIn.setFromValue(0.0);
            fadeIn.setToValue(1.0);
            
            stage.setScene(scene);
            fadeIn.play();
        } catch (IOException e) {
            showAlert("Erreur", "Erreur lors du chargement de l'accueil: " + e.getMessage(), Alert.AlertType.ERROR);
            e.printStackTrace();
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
