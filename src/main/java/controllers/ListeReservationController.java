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
            VBox card = new VBox(5);
            card.getStyleClass().add("white-bg");
            card.setPadding(new Insets(10));

            // Récupérer l'événement associé (image)
            Evenement evenement = null;
            try {
                for (Evenement ev : serviceEvenement.recuperer()) {
                    if (ev.getId_evenement() == reservation.getId_evenement()) {
                        evenement = ev;
                        break;
                    }
                }
            } catch (Exception e) {}
            if (evenement != null && evenement.getImage() != null && !evenement.getImage().isEmpty()) {
                try {
                    ImageView imageView = new ImageView(new Image(evenement.getImage()));
                    imageView.setFitWidth(100);
                    imageView.setFitHeight(100);
                    imageView.setPreserveRatio(true);
                    card.getChildren().add(imageView);
                } catch (Exception e) {}
            }

            Label nomLabel = new Label("Nom : " + reservation.getNom());
            Label prenomLabel = new Label("Prénom : " + reservation.getPrenom());
            Label emailLabel = new Label("Email : " + reservation.getEmail());
            Label dateLabel = new Label("Date : " + reservation.getDate());
            card.getChildren().addAll(nomLabel, prenomLabel, emailLabel, dateLabel);

            // Style de sélection
            if (reservation == selectedReservation) {
                card.setStyle("-fx-border-color: #0078D7; -fx-border-width: 2px;");
            } else {
                card.setStyle("");
            }

            // Gestion du clic pour sélectionner
            card.setOnMouseClicked(event -> {
                selectedReservation = reservation;
                afficherReservations(reservations); // Rafraîchir pour mettre à jour le style
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