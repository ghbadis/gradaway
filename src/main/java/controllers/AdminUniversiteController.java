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
    private Button modifierButton;
    @FXML
    private Button afficherButton;
    @FXML
    private Button supprimerButton;
    @FXML
    private Button logoutButton;
    @FXML
    private Button cardsButton;
    
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Initialize the controller
    }
    
    @FXML
    private void handleAjouterButton() {
        openWindow("ajouteruniversite.fxml", "Ajouter une Université");
    }
    
    @FXML
    private void handleModifierButton() {
        openWindow("modifieruniversite.fxml", "Modifier une Université");
    }
    
    @FXML
    private void handleAfficherButton() {
        openWindow("recupereruniversite.fxml", "Liste des Universités");
    }
    
    @FXML
    private void handleSupprimerButton() {
        openWindow("supprimeruniversite.fxml", "Supprimer une Université");
    }
    
    @FXML
    private void handleCardsButton() {
        openWindow("listuniversitecards.fxml", "Universités Disponibles");
    }
    
    @FXML
    private void handleLogoutButton() {
        // Close the current window
        Stage stage = (Stage) logoutButton.getScene().getWindow();
        stage.close();
        
        // You could add code here to return to a login screen if needed
    }
    
    private void openWindow(String fxmlFile, String title) {
        try {
            // Try different ways to load the FXML
            URL resourceUrl = getClass().getResource("/" + fxmlFile);
            
            if (resourceUrl == null) {
                // Try without the leading slash
                resourceUrl = getClass().getResource(fxmlFile);
            }
            
            if (resourceUrl == null) {
                // Try from the class loader
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
            
            // Debug output to see where resources are being loaded from
            System.out.println("Successfully loaded: " + resourceUrl.toString());
            
        } catch (IOException e) {
            e.printStackTrace();
            
            // Show error dialog with details
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error Opening Window");
            alert.setHeaderText("Failed to open " + title);
            alert.setContentText("Error: " + e.getMessage() + "\n\nCheck console for details.");
            alert.showAndWait();
            
            System.err.println("Error loading " + fxmlFile + ": " + e.getMessage());
        }
    }
} 