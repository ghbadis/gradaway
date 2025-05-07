package controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class AdminUniversiteController implements Initializable {

    @FXML
    private Button ajouterButton;
    @FXML
    private Button afficherButton;
    @FXML
    private Button logoutButton;
    
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Initialize the controller
    }
    
    @FXML
    private void handleAjouterButton() {
        openWindow("ajouteruniversite.fxml", "Ajouter une Université");
    }
    
    @FXML
    private void handleAfficherButton() {
        openWindow("recupereruniversite.fxml", "Liste des Universités");
    }
    
    @FXML
    private void handleLogoutButton() {
        Stage stage = (Stage) logoutButton.getScene().getWindow();
        stage.close();
    }
    
    private void openWindow(String fxmlFile, String title) {
        try {
            URL resourceUrl = getClass().getResource("/" + fxmlFile);
            
            if (resourceUrl == null) {
                resourceUrl = getClass().getResource(fxmlFile);
            }
            
            if (resourceUrl == null) {
                resourceUrl = getClass().getClassLoader().getResource(fxmlFile);
            }
            
            if (resourceUrl == null) {
                throw new IOException("Could not find resource: " + fxmlFile);
            }
            
            FXMLLoader loader = new FXMLLoader(resourceUrl);
            Parent root = loader.load();
            
            Stage stage = new Stage();
            stage.setTitle(title);
            stage.setScene(new Scene(root));
            stage.show();
            
            System.out.println("Successfully loaded: " + resourceUrl.toString());
            
        } catch (IOException e) {
            e.printStackTrace();
            
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error Opening Window");
            alert.setHeaderText("Failed to open " + title);
            alert.setContentText("Error: " + e.getMessage() + "\n\nCheck console for details.");
            alert.showAndWait();
            
            System.err.println("Error loading " + fxmlFile + ": " + e.getMessage());
        }
    }
} 