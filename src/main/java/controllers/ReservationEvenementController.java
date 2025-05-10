package controllers;

import entities.Evenement;
import entities.User;
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
import Services.ServiceEvenement;
import Services.ServiceUser;
import utils.EmailUtil;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
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
    private User currentUser;
    private final ServiceUser serviceUser = new ServiceUser();
    private final ServiceEvenement serviceEvenement = new ServiceEvenement();
    private ServiceReservationEvenement serviceReservation;
    private DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @FXML
    public void initialize() {
        serviceReservation = new ServiceReservationEvenement();
        // Désactiver l'édition de tous les champs
        nom_txtf.setEditable(false);
        prenom_txtf.setEditable(false);
        email_txtf.setEditable(false);
        date_picker.setEditable(false);
        date_picker.setDisable(true); // Désactiver complètement le DatePicker

        // Configurer les actions des boutons
        reserver_button.setOnAction(event -> reserverEvenement());
        afficher_mes_reservation_butt.setOnAction(event -> afficherReservations());
    }

    public void setEvenement(Evenement evenement) {
        this.evenement = evenement;
        // Pré-remplir la date de l'événement
        if (evenement != null && evenement.getDate() != null) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            LocalDate date = LocalDate.parse(evenement.getDate(), formatter);
            date_picker.setValue(date);
        }
    }

    public void setCurrentUser(User user) {
        this.currentUser = user;
        // Pré-remplir les informations de l'utilisateur
        if (user != null) {
            nom_txtf.setText(user.getNom());
            prenom_txtf.setText(user.getPrenom());
            email_txtf.setText(user.getEmail());
        }
    }

    private void reserverEvenement() {
        try {
            // Vérifier les champs obligatoires
            if (nom_txtf.getText().isEmpty() || prenom_txtf.getText().isEmpty() ||
                    email_txtf.getText().isEmpty() || date_picker.getValue() == null) {
                showAlert("Erreur", "Champs incomplets", "Veuillez remplir tous les champs obligatoires");
                return;
            }

            // Vérifier que l'utilisateur est connecté
            if (currentUser == null) {
                showAlert("Erreur", "Utilisateur non connecté", "Vous devez être connecté pour effectuer une réservation");
                return;
            }

            // Créer la réservation
            String date = date_picker.getValue().format(dateFormatter);
            ReservationEvenement reservation = new ReservationEvenement(
                    currentUser.getId(), // Utiliser l'ID de l'utilisateur connecté
                    evenement.getId_evenement(),
                    email_txtf.getText(),
                    nom_txtf.getText(),
                    prenom_txtf.getText(),
                    date
            );

            serviceReservation.ajouter(reservation);

            // Envoyer l'email de confirmation
            EmailUtil.sendConfirmationEmail(
                    email_txtf.getText(),
                    nom_txtf.getText(),
                    prenom_txtf.getText(),
                    evenement.getNom(),
                    date
            );

            showAlert("Succès", "Réservation effectuée", "Votre réservation a été enregistrée avec succès. Un email de confirmation vous a été envoyé.");
            fermerFenetre();
        } catch (SQLException e) {
            showAlert("Erreur", "Erreur lors de la réservation", e.getMessage());
        }
    }

    private void afficherReservations() {
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