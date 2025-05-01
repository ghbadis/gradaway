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
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import Services.ServiceReservationEvenement;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class ListeReservationController {
    @FXML
    private TableView<ReservationEvenement> reservation_tableview;
    @FXML
    private TableColumn<ReservationEvenement, Integer> id_reservation_col;
    @FXML
    private TableColumn<ReservationEvenement, Integer> id_evenement_col;
    @FXML
    private TableColumn<ReservationEvenement, String> email_col;
    @FXML
    private TableColumn<ReservationEvenement, String> nom_col;
    @FXML
    private TableColumn<ReservationEvenement, String> prenom_col;
    @FXML
    private TableColumn<ReservationEvenement, String> date_col;
    @FXML
    private Button supprimer_button;
    @FXML
    private Button modifier_button;

    private ServiceReservationEvenement serviceReservation;
    private ObservableList<ReservationEvenement> reservationsList;

    @FXML
    public void initialize() {
        serviceReservation = new ServiceReservationEvenement();
        reservationsList = FXCollections.observableArrayList();

        // Initialiser les colonnes du TableView
        id_reservation_col.setCellValueFactory(new PropertyValueFactory<>("id_reservation"));
        id_evenement_col.setCellValueFactory(new PropertyValueFactory<>("id_evenement"));
        email_col.setCellValueFactory(new PropertyValueFactory<>("email"));
        nom_col.setCellValueFactory(new PropertyValueFactory<>("nom"));
        prenom_col.setCellValueFactory(new PropertyValueFactory<>("prenom"));
        date_col.setCellValueFactory(new PropertyValueFactory<>("date"));

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
            reservationsList.clear();
            reservationsList.addAll(reservations);
            reservation_tableview.setItems(reservationsList);
        } catch (SQLException e) {
            showAlert("Erreur", "Erreur lors du chargement des réservations", e.getMessage());
        }
    }

    private void supprimerReservation() {
        ReservationEvenement selectedReservation = reservation_tableview.getSelectionModel().getSelectedItem();
        if (selectedReservation == null) {
            showAlert("Erreur", "Aucune réservation sélectionnée", "Veuillez sélectionner une réservation à supprimer");
            return;
        }

        try {
            serviceReservation.supprimer(selectedReservation);
            loadData();
            showAlert("Succès", "Réservation supprimée", "La réservation a été supprimée avec succès");
        } catch (SQLException e) {
            showAlert("Erreur", "Erreur lors de la suppression", e.getMessage());
        }
    }

    private void modifierReservation() {
        ReservationEvenement selectedReservation = reservation_tableview.getSelectionModel().getSelectedItem();
        if (selectedReservation == null) {
            showAlert("Erreur", "Aucune réservation sélectionnée", "Veuillez sélectionner une réservation à modifier");
            return;
        }

        try {
            // Charger la fenêtre de modification
            FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("modifier_reservation.fxml"));
            Parent root = loader.load();

            // Configurer le contrôleur
            ModifierReservationController controller = loader.getController();
            controller.setReservation(selectedReservation);
            controller.setParentController(this);

            // Créer et afficher la fenêtre
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