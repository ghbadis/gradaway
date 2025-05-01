package controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class DashboardController implements Initializable {

    @FXML
    private Button nouvelleCandidatureButton;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        System.out.println("Dashboard initialized");
    }
    
    // University Management Methods
    
    @FXML
    private void handleAjouterUniversite() {
        try {
            openWindow("ajouteruniversite.fxml", "Ajouter Université");
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible d'ouvrir la fenêtre", e.getMessage());
        }
    }
    
    @FXML
    private void handleModifierUniversite() {
        try {
            openWindow("modifieruniversite.fxml", "Modifier Université");
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible d'ouvrir la fenêtre", e.getMessage());
        }
    }
    
    @FXML
    private void handleAfficherUniversite() {
        try {
            openWindow("recupereruniversite.fxml", "Liste des Universités");
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible d'ouvrir la fenêtre", e.getMessage());
        }
    }
    
    @FXML
    private void handleSupprimerUniversite() {
        try {
            openWindow("supprimeruniversite.fxml", "Supprimer Université");
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible d'ouvrir la fenêtre", e.getMessage());
        }
    }
    
    // Candidature Management Methods
    
    @FXML
    private void handleNouvelleCandidature() {
        try {
            openWindow("supprimerconditures.fxml", "Supprimer Candidature");
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible d'ouvrir la fenêtre", e.getMessage());
        }
    }
    
    @FXML
    private void handleMesCandidatures() {
        try {
            openWindow("afficherconditures.fxml", "Mes Candidatures");
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible d'ouvrir la fenêtre", e.getMessage());
        }
    }
    
    @FXML
    private void handleRechercherUniversite() {
        try {
            openWindow("listuniversitecards.fxml", "Rechercher Université");
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible d'ouvrir la fenêtre", e.getMessage());
        }
    }
    
    @FXML
    private void handleSuiviCandidatures() {
        try {
            openWindow("suivicandidatures.fxml", "Suivi des Candidatures");
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible d'ouvrir la fenêtre", e.getMessage());
        }
    }
    
    @FXML
    private void handleQuitButton() {
        System.exit(0);
    }
    
    // Helper Methods
    
    private void openWindow(String fxmlFile, String title) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/" + fxmlFile));
        Parent root = loader.load();
        Stage stage = new Stage();
        stage.setTitle(title);
        stage.setScene(new Scene(root));
        stage.show();
    }
    
    private void showAlert(Alert.AlertType alertType, String title, String header, String content) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }
} 