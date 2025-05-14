package controllers;

import entities.ReservationEvenement;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import Services.ServiceReservationEvenement;

import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

public class ModifierReservationController implements Initializable {
    @FXML
    private TextField email_txtf;
    @FXML
    private TextField nom_txtf;
    @FXML
    private TextField prenom_txtf;
    @FXML
    private DatePicker date_picker;
    @FXML
    private Button annuler_button;
    @FXML
    private Button enregistrer_button;

    private ReservationEvenement reservation;
    private ServiceReservationEvenement serviceReservation;
    private ListeReservationController parentController;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        serviceReservation = new ServiceReservationEvenement();

        // Empêcher la sélection de dates passées
        date_picker.setDayCellFactory(picker -> new javafx.scene.control.DateCell() {
            @Override
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                LocalDate today = LocalDate.now();
                setDisable(empty || date.isBefore(today));
            }
        });
        
        // Configurer les actions des boutons
        annuler_button.setOnAction(event -> fermerFenetre());
        enregistrer_button.setOnAction(event -> enregistrerModifications());
    }

    public void setReservation(ReservationEvenement reservation) {
        this.reservation = reservation;
        chargerDonnees();
    }

    public void setParentController(ListeReservationController controller) {
        this.parentController = controller;
    }

    private void chargerDonnees() {
        if (reservation != null) {
            email_txtf.setText(reservation.getEmail());
            nom_txtf.setText(reservation.getNom());
            prenom_txtf.setText(reservation.getPrenom());
            
            // Convertir la date en LocalDate pour le DatePicker
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            LocalDate date = LocalDate.parse(reservation.getDate(), formatter);
            date_picker.setValue(date);
        }
    }

    private void enregistrerModifications() {
        try {
            // Contrôle de saisie : tous les champs doivent être remplis
            String email = email_txtf.getText();
            String nom = nom_txtf.getText();
            String prenom = prenom_txtf.getText();
            LocalDate dateValue = date_picker.getValue();
            if (email.isEmpty() || nom.isEmpty() || prenom.isEmpty() || dateValue == null) {
                showAlert("Erreur", "Champs obligatoires", "Veuillez remplir tous les champs avant d'enregistrer la réservation.");
                return;
            }

            // Mettre à jour les données de la réservation
            reservation.setEmail(email);
            reservation.setNom(nom);
            reservation.setPrenom(prenom);
            reservation.setDate(dateValue.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));

            // Sauvegarder les modifications
            serviceReservation.modifier(reservation);

            // Rafraîchir la liste dans le contrôleur parent
            if (parentController != null) {
                parentController.refreshData();
            }

            // Fermer la fenêtre
            fermerFenetre();
            
            // Afficher un message de succès
            showAlert("Succès", "Réservation modifiée", "La réservation a été modifiée avec succès");
        } catch (SQLException e) {
            showAlert("Erreur", "Erreur lors de la modification", e.getMessage());
        }
    }

    private void fermerFenetre() {
        Stage stage = (Stage) annuler_button.getScene().getWindow();
        stage.close();
    }

    private void showAlert(String title, String header, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }
} 