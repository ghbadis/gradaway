package controllers;

import entities.Evenement;
import entities.ReservationEvenement;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import Services.ServiceReservationEvenement;

import java.io.IOException;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;

public class ReservationEvenementController {
    @FXML
    private TextField nom_txtf;
    @FXML
    private TextField prenom_txtf;
    @FXML
    private TextField email_txtf;
    @FXML
    private DatePicker date_picker;
    @FXML
    private Button reserver_button;
    @FXML
    private Button afficher_mes_reservation_butt;

    private Evenement evenement;
    private ServiceReservationEvenement serviceReservation;
    private DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @FXML
    public void initialize() {
        serviceReservation = new ServiceReservationEvenement();
        setupListeners();
    }

    public void setEvenement(Evenement evenement) {
        this.evenement = evenement;
        // Mettre à jour l'interface avec les informations de l'événement
        nom_txtf.setPromptText("Nom");
        prenom_txtf.setPromptText("Prénom");
        email_txtf.setPromptText("Email");
    }

    private void setupListeners() {
        reserver_button.setOnAction(event -> reserverEvenement());
        afficher_mes_reservation_butt.setOnAction(event -> ouvrirListeReservations());
    }

    private void reserverEvenement() {
        try {
            // Vérifier les champs obligatoires
            if (nom_txtf.getText().isEmpty() || prenom_txtf.getText().isEmpty() || 
                email_txtf.getText().isEmpty() || date_picker.getValue() == null) {
                showAlert("Erreur", "Champs incomplets", "Veuillez remplir tous les champs obligatoires");
                return;
            }

            // Créer la réservation
            String date = date_picker.getValue().format(dateFormatter);
            ReservationEvenement reservation = new ReservationEvenement(
                1, // id_etudiant (à remplacer par l'ID réel de l'étudiant connecté)
                evenement.getId_evenement(),
                email_txtf.getText(),
                nom_txtf.getText(),
                prenom_txtf.getText(),
                date
            );

            serviceReservation.ajouter(reservation);
            showAlert("Succès", "Réservation effectuée", "Votre réservation a été enregistrée avec succès");
            fermerFenetre();
        } catch (SQLException e) {
            showAlert("Erreur", "Erreur lors de la réservation", e.getMessage());
        }
    }

    private void ouvrirListeReservations() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/liste_reservation.fxml"));
            Parent root = loader.load();
            
            Stage stage = new Stage();
            stage.setTitle("Mes Réservations");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException | RuntimeException e) {
            e.printStackTrace(); // Affiche le stacktrace dans la console
            showAlert("Erreur", "Erreur lors de l'ouverture de la liste des réservations", e.toString() + "\n" + (e.getCause() != null ? e.getCause().toString() : ""));
        }
    }

    private void fermerFenetre() {
        reserver_button.getScene().getWindow().hide();
    }

    private void showAlert(String title, String header, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }
} 