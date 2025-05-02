package controllers;

import entities.ReservationEvenement;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import Services.ServiceReservationEvenement;
import javafx.geometry.Insets;
import Services.ServiceEvenement;
import entities.Evenement;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.geometry.Pos;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class ListeReservationController {
    @FXML
    private VBox reservations_container;
    @FXML
    private Button supprimer_button;
    @FXML
    private Button modifier_button;

    private ServiceReservationEvenement serviceReservation;
    private ObservableList<ReservationEvenement> reservationsList;
    private ServiceEvenement serviceEvenement = new ServiceEvenement();
    private ReservationEvenement selectedReservation = null;

    @FXML
    public void initialize() {
        serviceReservation = new ServiceReservationEvenement();
        reservationsList = FXCollections.observableArrayList();

        // Charger les données
        loadData();

        // Ajouter les listeners pour les boutons
        supprimer_button.setOnAction(event -> supprimerReservation());
        modifier_button.setOnAction(event -> modifierReservation());
    }

    public void refreshData() {
        loadData();
    }

    private void loadData() {
        try {
            List<ReservationEvenement> reservations = serviceReservation.recuperer();
            afficherReservations(reservations);
        } catch (SQLException e) {
            showAlert("Erreur", "Erreur lors du chargement des réservations", e.getMessage());
        }
    }

    private void afficherReservations(List<ReservationEvenement> reservations) {
        reservations_container.getChildren().clear();
        for (ReservationEvenement reservation : reservations) {
            HBox card = new HBox(15);
            card.getStyleClass().addAll("white-bg", "shadow", "card");
            card.setPadding(new Insets(16));
            card.setAlignment(Pos.CENTER_LEFT);

            // Image de l'événement
            Evenement evenement = null;
            try {
                for (Evenement ev : serviceEvenement.recuperer()) {
                    if (ev.getId_evenement() == reservation.getId_evenement()) {
                        evenement = ev;
                        break;
                    }
                }
            } catch (Exception e) {}

            ImageView imageView = new ImageView();
            if (evenement != null && evenement.getImage() != null && !evenement.getImage().isEmpty()) {
                try {
                    imageView.setImage(new Image(evenement.getImage()));
                } catch (Exception e) {}
            }
            imageView.setFitWidth(80);
            imageView.setFitHeight(80);
            imageView.setPreserveRatio(true);
            imageView.setSmooth(true);
            imageView.setStyle("-fx-background-radius: 10px; -fx-border-radius: 10px;");

            VBox infoBox = new VBox(5);
            Label nomLabel = new Label("Nom : " + reservation.getNom());
            nomLabel.getStyleClass().add("form-label");
            Label prenomLabel = new Label("Prénom : " + reservation.getPrenom());
            prenomLabel.getStyleClass().add("form-label");
            Label emailLabel = new Label("Email : " + reservation.getEmail());
            emailLabel.setStyle("-fx-text-fill: #666;");
            Label dateLabel = new Label("Date : " + reservation.getDate());
            dateLabel.setStyle("-fx-text-fill: #666;");
            infoBox.getChildren().addAll(nomLabel, prenomLabel, emailLabel, dateLabel);

            card.getChildren().addAll(imageView, infoBox);

            // Style de sélection
            if (reservation == selectedReservation) {
                card.setStyle("-fx-border-color: #1976D2; -fx-border-width: 2px; -fx-background-radius: 15px;");
            } else {
                card.setStyle("-fx-background-radius: 15px;");
            }

            card.setOnMouseClicked(event -> {
                selectedReservation = reservation;
                afficherReservations(reservations);
            });

            reservations_container.getChildren().add(card);
        }
    }

    private void supprimerReservation() {
        if (selectedReservation == null) {
            showAlert("Erreur", "Aucune réservation sélectionnée", "Veuillez sélectionner une réservation à supprimer");
            return;
        }
        try {
            serviceReservation.supprimer(selectedReservation);
            selectedReservation = null;
            loadData();
            showAlert("Succès", "Réservation supprimée", "La réservation a été supprimée avec succès");
        } catch (SQLException e) {
            showAlert("Erreur", "Erreur lors de la suppression", e.getMessage());
        }
    }

    private void modifierReservation() {
        if (selectedReservation == null) {
            showAlert("Erreur", "Aucune réservation sélectionnée", "Veuillez sélectionner une réservation à modifier");
            return;
        }
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("modifier_reservation.fxml"));
            Parent root = loader.load();
            ModifierReservationController controller = loader.getController();
            controller.setReservation(selectedReservation);
            controller.setParentController(this);
            Stage stage = new Stage();
            stage.setTitle("Modifier la réservation");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            showAlert("Erreur", "Erreur lors de l'ouverture de la fenêtre de modification", e.getMessage());
        }
    }

    private void showAlert(String title, String header, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }
} 