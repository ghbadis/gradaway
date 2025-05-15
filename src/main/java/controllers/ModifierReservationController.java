package controllers;

import entities.ReservationEvenement;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import Services.ServiceReservationEvenement;
import javafx.event.ActionEvent;

import java.io.IOException;
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
    private int currentUserId;

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

    public void setCurrentUserId(int userId) {
        this.currentUserId = userId;
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

    @FXML
    public void onAccueilButtonClick(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/acceuil.fxml"));
            Parent root = loader.load();

            Acceuilcontroller controller = loader.getController();
            controller.setUserId(currentUserId);

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
        if (currentUserId <= 0) {
            showAlert("Erreur", "ID utilisateur invalide", "Impossible d'ouvrir le profil.");
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/EditProfile.fxml"));
            Parent root = loader.load();

            EditProfileController controller = loader.getController();
            controller.setUserId(currentUserId);

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
            controller.setEtudiantId(currentUserId);

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
            controller.setCurrentUserId(currentUserId);

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