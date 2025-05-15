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
import javafx.event.ActionEvent;

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
        } catch (SQLException e) {
            showAlert("Erreur", "Erreur lors de la réservation", e.getMessage());
        }
    }

    private void afficherReservations() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/liste_reservation.fxml"));
            Parent root = loader.load();

            // Récupérer le contrôleur et lui passer l'ID de l'utilisateur
            ListeReservationController controller = loader.getController();
            controller.setCurrentUserId(currentUser.getId());

            Stage stage = (Stage) afficher_mes_reservation_butt.getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Mes Réservations");
            stage.centerOnScreen();
        } catch (IOException | RuntimeException e) {
            e.printStackTrace();
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

    @FXML
    public void onAccueilButtonClick(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/acceuil.fxml"));
            Parent root = loader.load();

            Acceuilcontroller controller = loader.getController();
            controller.setUserId(currentUser.getId());

            Stage stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Accueil - GradAway");
            stage.centerOnScreen();

        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Erreur", "Erreur lors de l'ouverture de la page d'accueil", e.getMessage());
        }
    }

    @FXML
    public void onProfileButtonClick(ActionEvent event) {
        if (currentUser == null) {
            showAlert("Erreur", "Utilisateur non connecté", "Impossible d'ouvrir le profil.");
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/EditProfile.fxml"));
            Parent root = loader.load();

            EditProfileController controller = loader.getController();
            controller.setUserId(currentUser.getId());

            Stage stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Modifier Mon Profil");
            stage.centerOnScreen();

        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Erreur", "Erreur lors de l'ouverture du profil", e.getMessage());
        }
    }

    @FXML
    public void ondossierButtonClick(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AjoutDossier.fxml"));
            Parent root = loader.load();

            AjoutDossierController controller = loader.getController();
            controller.setEtudiantId(currentUser.getId());

            Stage stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Ajout Dossier");
            stage.centerOnScreen();

        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Erreur", "Erreur lors de l'ouverture du dossier", e.getMessage());
        }
    }

    @FXML
    public void onuniversiteButtonClick(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/adminconditature.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Gestion des Candidatures");
            stage.centerOnScreen();

        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Erreur", "Erreur lors de l'ouverture des candidatures", e.getMessage());
        }
    }

    @FXML
    public void onentretienButtonClick(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/DemanderEntretien.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Demander Entretien");
            stage.centerOnScreen();

        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Erreur", "Erreur lors de l'ouverture des entretiens", e.getMessage());
        }
    }

    @FXML
    public void onevenementButtonClick(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/affiche_evenement.fxml"));
            Parent root = loader.load();

            Ajouterafficheevenementcontrolleur controller = loader.getController();
            controller.setCurrentUserId(currentUser.getId());

            Stage stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Événements");
            stage.centerOnScreen();

        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Erreur", "Erreur lors de l'ouverture des événements", e.getMessage());
        }
    }

    @FXML
    public void onhebergementButtonClick(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ListFoyerClient.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Liste des Foyers");
            stage.centerOnScreen();

        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Erreur", "Erreur lors de l'ouverture des foyers", e.getMessage());
        }
    }

    @FXML
    public void onrestaurantButtonClick(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ListRestaurantClient.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Liste des Restaurants");
            stage.centerOnScreen();

        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Erreur", "Erreur lors de l'ouverture des restaurants", e.getMessage());
        }
    }

    @FXML
    public void onvolsButtonClick(ActionEvent event) {
        showAlert("Information", "Information", "La fonctionnalité des vols sera bientôt disponible.");
    }

    @FXML
    public void onlogoutButtonClick(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/login-view.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Login - GradAway");
            stage.centerOnScreen();

        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Erreur", "Erreur lors de la déconnexion", e.getMessage());
        }
    }
} 