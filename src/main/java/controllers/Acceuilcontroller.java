package controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.stage.Stage;

import java.io.IOException;

public class Acceuilcontroller {

    private int userId;

    @FXML
    private Button scheduleEntretienButton;
    @FXML
    private Label registeredEventsLabel;
    @FXML
    private Label phoneLabel;
    @FXML
    private Button checkMessagesButton;
    @FXML
    private Label activeDossiersLabel;
    @FXML
    private ListView appointmentsListView;
    @FXML
    private Label addressLabel;
    @FXML
    private Button viewEventsButton;
    @FXML
    private ListView notificationsListView;
    @FXML
    private Label emailLabel;
    @FXML
    private Button addDossierButton;
    @FXML
    private Label upcomingInterviewsLabel;

    public void setUserId(int userId) {
        this.userId = userId;
        System.out.println("Acceuilcontroller: Received user ID: " + this.userId);
    }

    @FXML
    public void onhebergementButtonClick(ActionEvent actionEvent) {

    }

    @FXML
    public void onentretienButtonClick(ActionEvent actionEvent) {
    }

    @FXML
    public void onuniversiteButtonClick(ActionEvent actionEvent) {
    }

    @FXML
    public void onvolsButtonClick(ActionEvent actionEvent) {
    }

    @FXML
    public void ondossierButtonClick(ActionEvent event) {
        if (this.userId <= 0) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "ID utilisateur invalide. Impossible d'ouvrir la gestion des dossiers.");
            return;
        }

        try {
            System.out.println("AcceuilController: Opening AjoutDossier view for User ID: " + this.userId);
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AjoutDossier.fxml"));
            Parent root = loader.load();

            AjoutDossierController ajoutDossierController = loader.getController();
            ajoutDossierController.setEtudiantId(this.userId); // Pass the user ID

            Stage stage = new Stage();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Gestion du Dossier");
            stage.setMinWidth(1200); // Match the size from LoginViewcontroller or adjust
            stage.setMinHeight(800);
            stage.setResizable(true);
            stage.centerOnScreen();
            stage.show();

            // Optional: Close the accueil window if needed
            // Stage currentStage = (Stage) ((Button) event.getSource()).getScene().getWindow();
            // currentStage.close();

        } catch (IOException e) {
            System.err.println("AcceuilController: Error loading AjoutDossier.fxml: " + e.getMessage());
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors de l'ouverture de la vue du dossier.");
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("AcceuilController: Unexpected error: " + e.getMessage());
            showAlert(Alert.AlertType.ERROR, "Erreur Inattendue", "Une erreur inattendue est survenue.");
            e.printStackTrace();
        }
    }

    @FXML
    public void onlogoutButtonClick(ActionEvent actionEvent) {
    }

    @FXML
    public void onProfileButtonClick(ActionEvent event) {
        if (this.userId <= 0) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "ID utilisateur invalide. Impossible d'ouvrir le profil.");
            return;
        }

        try {
            System.out.println("AcceuilController: Opening EditProfile view for User ID: " + this.userId);
            // Assuming the FXML file is named EditProfile.fxml
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/EditProfile.fxml"));
            Parent root = loader.load();

            EditProfileController editProfileController = loader.getController();
            editProfileController.setUserId(this.userId); // Pass the user ID

            Stage stage = new Stage();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Modifier Mon Profil");
            // Adjust size as needed for EditProfile.fxml
            stage.setMinWidth(800);
            stage.setMinHeight(600);
            stage.setResizable(true);
            stage.centerOnScreen();
            stage.show();

            // Optional: Close the accueil window if needed
            // Stage currentStage = (Stage) ((Button) event.getSource()).getScene().getWindow();
            // currentStage.close();

        } catch (IOException e) {
            System.err.println("AcceuilController: Error loading EditProfile.fxml: " + e.getMessage());
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors de l'ouverture de la vue du profil.");
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("AcceuilController: Unexpected error opening profile: " + e.getMessage());
            showAlert(Alert.AlertType.ERROR, "Erreur Inattendue", "Une erreur inattendue est survenue.");
            e.printStackTrace();
        }
    }

    @FXML
    public void onevenementButtonClick(ActionEvent actionEvent) {
    }

    @FXML
    public void onrestaurantButtonClick(ActionEvent actionEvent) {
    }

    @FXML
    public void onAccueilButtonClick(ActionEvent actionEvent) {
    }

    // Helper method for showing alerts (can be reused)
    private void showAlert(Alert.AlertType alertType, String title, String content) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
