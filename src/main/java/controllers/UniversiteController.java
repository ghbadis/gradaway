package controllers;

import entities.Universite;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import Services.ServiceUniversite;

import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class UniversiteController implements Initializable {

    @FXML
    private TextField nomField;
    @FXML
    private TextField villeField;
    @FXML
    private TextField adresseField;
    @FXML
    private TextField domaineField;
    @FXML
    private TextField fraisField;
    @FXML
    private Button ajouterButton;
    @FXML
    private Button fermerButton;
    
    private final ServiceUniversite serviceUniversite = new ServiceUniversite();
    
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Initialize controller
    }
    
    @FXML
    private void handleAjouterButton() {
        if (validateFields()) {
            try {
                String nom = nomField.getText();
                String ville = villeField.getText();
                String adresse = adresseField.getText();
                String domaine = domaineField.getText();
                double frais = Double.parseDouble(fraisField.getText());
                
                // Create Universite object
                Universite universite = new Universite(nom, ville, adresse, domaine, frais);
                
                // Call service to add universite
                serviceUniversite.ajouter(universite);
                
                // Show success message
                showAlert(Alert.AlertType.INFORMATION, "Succès", "Université ajoutée avec succès!");
                
                // Clear the form
                clearFields();
                
            } catch (NumberFormatException e) {
                showAlert(Alert.AlertType.ERROR, "Erreur de format", "Les frais doivent être un nombre valide");
            } catch (SQLException e) {
                showAlert(Alert.AlertType.ERROR, "Erreur de base de données", e.getMessage());
            }
        }
    }
    
    @FXML
    private void handleFermerButton() {
        // Close the current window and return to the admin interface
        Stage stage = (Stage) fermerButton.getScene().getWindow();
        stage.close();
    }
    
    private boolean validateFields() {
        if (nomField.getText().isEmpty() || 
            villeField.getText().isEmpty() || 
            adresseField.getText().isEmpty() || 
            domaineField.getText().isEmpty() || 
            fraisField.getText().isEmpty()) {
            
            showAlert(Alert.AlertType.ERROR, "Erreur de validation", "Tous les champs sont obligatoires");
            return false;
        }
        
        try {
            Double.parseDouble(fraisField.getText());
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur de format", "Les frais doivent être un nombre valide");
            return false;
        }
        
        return true;
    }
    
    private void clearFields() {
        nomField.clear();
        villeField.clear();
        adresseField.clear();
        domaineField.clear();
        fraisField.clear();
    }
    
    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
} 