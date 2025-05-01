package controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import java.io.IOException;

public class AdminConditatureController {

    @FXML
    private Button ajouterButton;

    @FXML
    private Button afficherButton;

    @FXML
    private Button supprimerButton;

    @FXML
    private Button logoutButton;

    @FXML
    void handleAjouterButton(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/listuniversitecards.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) ajouterButton.getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            showAlert("Erreur", "Impossible de charger l'écran des universités.", e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    void handleAfficherButton(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/afficherconditures.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) afficherButton.getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            showAlert("Erreur", "Impossible de charger l'écran d'affichage des candidatures.", e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    void handleSupprimerButton(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/supprimerconditures.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) supprimerButton.getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            showAlert("Erreur", "Impossible de charger l'écran de suppression des candidatures.", e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    void handleLogoutButton(ActionEvent event) {
        try {
            // Close the current window
            Stage stage = (Stage) logoutButton.getScene().getWindow();
            stage.close();
            
            // Create a new primary stage (this mimics logging out and starting fresh)
            Stage primaryStage = new Stage();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/adminconditature.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root);
            primaryStage.setScene(scene);
            primaryStage.setTitle("Gradaway - Administration");
            primaryStage.show();
        } catch (IOException e) {
            showAlert("Erreur", "Impossible de se déconnecter.", e.getMessage());
            e.printStackTrace();
        }
    }

    private void showAlert(String title, String header, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }
} 