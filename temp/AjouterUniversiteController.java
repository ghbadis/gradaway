package controllers;

import java.io.IOException;
import java.net.URL;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import javafx.stage.FileChooser;
import javafx.scene.image.Image;
import java.io.File;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.concurrent.Worker.State;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.stage.Modality;
import javafx.stage.Window;
import javafx.stage.WindowEvent;

import controls.AutocompleteTextField;
import utils.JavaConnector;
import utils.JavaConnector.MapLocation;
import models.Universite;

public class AjouterUniversiteController {

    @FXML private Button retourButton;
    @FXML private Button selectPhotoButton;
    @FXML private Button ajouterButton;
    @FXML private Button verifyAddressButton;
    @FXML private Button fermerButton;
    
    @FXML private TextField nomField;
    @FXML private TextField villeField;
    @FXML private TextField adresseField;
    @FXML private TextField domaineField;
    @FXML private TextField fraisField;
    @FXML private TextField latitudeField;
    @FXML private TextField longitudeField;

    @FXML private Label nomErrorLabel;
    @FXML private Label villeErrorLabel;
    @FXML private Label adresseErrorLabel;
    @FXML private Label domaineErrorLabel;
    @FXML private Label fraisErrorLabel;
    @FXML private Label coordsErrorLabel;
    @FXML private Label photoPathLabel;
    @FXML private Label addressStatusLabel;

    @FXML private ImageView photoPreview;

    private String selectedPhotoPath;
    private ExecutorService executorService;
    private boolean isAddressValid = false;
    private double[] coordinates = null;

    @FXML
    public void initialize() {
        try {
            // Initialize the executor service for background tasks
            executorService = Executors.newSingleThreadExecutor();
            
            // Add listeners to clear validation when fields change
            villeField.textProperty().addListener((observable, oldValue, newValue) -> {
                if (!newValue.equals(oldValue)) {
                    isAddressValid = false;
                    addressStatusLabel.setVisible(false);
                }
            });
            
            adresseField.textProperty().addListener((observable, oldValue, newValue) -> {
                if (!newValue.equals(oldValue)) {
                    isAddressValid = false;
                    addressStatusLabel.setVisible(false);
                }
            });
            
            // Set up coordinate field listeners
            latitudeField.textProperty().addListener((observable, oldValue, newValue) -> {
                updateCoordinatesFromFields();
            });
            
            longitudeField.textProperty().addListener((observable, oldValue, newValue) -> {
                updateCoordinatesFromFields();
            });
            
        } catch (Exception e) {
            System.err.println("Erreur d'initialisation: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Update the coordinates array from the latitude/longitude fields
     */
    private void updateCoordinatesFromFields() {
        try {
            String latText = latitudeField.getText().trim();
            String lngText = longitudeField.getText().trim();
            
            if (!latText.isEmpty() && !lngText.isEmpty()) {
                double lat = Double.parseDouble(latText);
                double lng = Double.parseDouble(lngText);
                
                // Validate coordinate ranges
                if (lat >= -90 && lat <= 90 && lng >= -180 && lng <= 180) {
                    coordinates = new double[]{lat, lng};
                    isAddressValid = true;
                    
                    // Update status
                    addressStatusLabel.setText("Coordonnées valides ✓");
                    addressStatusLabel.setTextFill(javafx.scene.paint.Color.web("#66ff66"));
                    addressStatusLabel.setVisible(true);
                    
                    // Hide error
                    coordsErrorLabel.setVisible(false);
                } else {
                    coordinates = null;
                    isAddressValid = false;
                    
                    // Show error
                    coordsErrorLabel.setText("Coordonnées hors limites");
                    coordsErrorLabel.setVisible(true);
                    addressStatusLabel.setVisible(false);
                }
            } else {
                // One or both fields are empty
                if (!latText.isEmpty() || !lngText.isEmpty()) {
                    // Only one field has data
                    coordsErrorLabel.setText("Les deux coordonnées sont requises");
                    coordsErrorLabel.setVisible(true);
                } else {
                    // Both fields are empty, clear error
                    coordsErrorLabel.setVisible(false);
                }
                
                coordinates = null;
                isAddressValid = false;
                addressStatusLabel.setVisible(false);
            }
        } catch (NumberFormatException e) {
            // Invalid number format
            coordinates = null;
            isAddressValid = false;
            coordsErrorLabel.setText("Format de coordonnées invalide");
            coordsErrorLabel.setVisible(true);
            addressStatusLabel.setVisible(false);
        }
    }

    @FXML
    private void handleRetourButton() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/recupereruniversite.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) retourButton.getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Liste des Universités");
            stage.show();
        } catch (IOException e) {
            // Handle error
            e.printStackTrace();
            showAlert(AlertType.ERROR, "Erreur de navigation", 
                     "Impossible de retourner à la liste des universités", 
                     e.getMessage());
        }
    }

    @FXML
    private void handleSelectPhotoButton() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Sélectionner une photo");
        fileChooser.getExtensionFilters().addAll(
            new FileChooser.ExtensionFilter("Images", "*.png", "*.jpg", "*.jpeg", "*.gif")
        );
        Stage stage = (Stage) selectPhotoButton.getScene().getWindow();
        File selectedFile = fileChooser.showOpenDialog(stage);

        if (selectedFile != null) {
            selectedPhotoPath = selectedFile.getAbsolutePath();
            photoPathLabel.setText(selectedFile.getName());
            Image image = new Image(selectedFile.toURI().toString());
            photoPreview.setImage(image);
        }
    }
    
    @FXML
    private void handleVerifyAddressButton() {
        // Manually verify the coordinates
        updateCoordinatesFromFields();
        
        if (coordinates != null) {
            isAddressValid = true;
            addressStatusLabel.setText("Coordonnées validées ✓");
            addressStatusLabel.setTextFill(javafx.scene.paint.Color.web("#66ff66"));
        } else {
            isAddressValid = false;
            addressStatusLabel.setText("Coordonnées invalides");
            addressStatusLabel.setTextFill(javafx.scene.paint.Color.web("#ff6666"));
        }
        addressStatusLabel.setVisible(true);
    }

    @FXML
    private void handleAjouterButton() {
        boolean isValid = validateInputs();
        
        if (isValid) {
            try {
                // Create university object
                Universite universite = createUniversiteFromInputs();
                
                // Save university to database (would be implemented in a service)
                saveUniversite(universite);
                
                // Show success message
                showAlert(AlertType.INFORMATION, "Université ajoutée", 
                          "L'université a été ajoutée avec succès !", null);
                
                // Return to university list
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/recupereruniversite.fxml"));
                Parent root = loader.load();
                Stage stage = (Stage) ajouterButton.getScene().getWindow();
                Scene scene = new Scene(root);
                stage.setScene(scene);
                stage.setTitle("Liste des Universités");
                stage.show();
            } catch (Exception e) {
                showAlert(AlertType.ERROR, "Erreur", 
                          "Une erreur est survenue lors de l'ajout", e.getMessage());
                e.printStackTrace();
            }
        }
    }
    
    private Universite createUniversiteFromInputs() {
        String nom = nomField.getText().trim();
        String ville = villeField.getText().trim();
        String adresse = adresseField.getText().trim();
        String domaine = domaineField.getText().trim();
        double frais = Double.parseDouble(fraisField.getText().trim());
        
        Universite universite = new Universite(nom, ville, adresse, domaine, frais, selectedPhotoPath);
        
        // Add coordinates if available
        if (coordinates != null) {
            universite.setLatitude(coordinates[0]);
            universite.setLongitude(coordinates[1]);
        }
        
        return universite;
    }
    
    private void saveUniversite(Universite universite) {
        // TODO: Implement actual database save
        // For now, just print the university to the console
        System.out.println("Saving university: " + universite);
        System.out.println("Coordinates: " + 
                          (coordinates != null ? coordinates[0] + ", " + coordinates[1] : "Not available"));
    }
    
    private void showAlert(AlertType type, String title, String header, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }
    
    private boolean validateInputs() {
        boolean isValid = true;
        
        // Validate Nom
        if (nomField.getText().trim().isEmpty()) {
            nomErrorLabel.setText("Le nom est obligatoire");
            nomErrorLabel.setVisible(true);
            isValid = false;
        } else {
            nomErrorLabel.setVisible(false);
        }
        
        // Validate Ville
        if (villeField.getText().trim().isEmpty()) {
            villeErrorLabel.setText("La ville est obligatoire");
            villeErrorLabel.setVisible(true);
            isValid = false;
        } else {
            villeErrorLabel.setVisible(false);
        }
        
        // Validate Adresse
        if (adresseField.getText().trim().isEmpty()) {
            adresseErrorLabel.setText("L'adresse est obligatoire");
            adresseErrorLabel.setVisible(true);
            isValid = false;
        } else {
            adresseErrorLabel.setVisible(false);
        }
        
        // Validate coordinates
        if (coordinates == null) {
            coordsErrorLabel.setText("Les coordonnées sont obligatoires");
            coordsErrorLabel.setVisible(true);
            isValid = false;
        } else {
            coordsErrorLabel.setVisible(false);
        }
        
        // Validate Domaine
        if (domaineField.getText().trim().isEmpty()) {
            domaineErrorLabel.setText("Le domaine est obligatoire");
            domaineErrorLabel.setVisible(true);
            isValid = false;
        } else {
            domaineErrorLabel.setVisible(false);
        }
        
        // Validate Frais
        try {
            if (fraisField.getText().trim().isEmpty()) {
                fraisErrorLabel.setText("Les frais sont obligatoires");
                fraisErrorLabel.setVisible(true);
                isValid = false;
            } else {
                double frais = Double.parseDouble(fraisField.getText().trim());
                if (frais < 0) {
                    fraisErrorLabel.setText("Les frais doivent être positifs");
                    fraisErrorLabel.setVisible(true);
                    isValid = false;
                } else {
                    fraisErrorLabel.setVisible(false);
                }
            }
        } catch (NumberFormatException e) {
            fraisErrorLabel.setText("Veuillez entrer un nombre valide");
            fraisErrorLabel.setVisible(true);
            isValid = false;
        }
        
        // Validate Photo
        if (selectedPhotoPath == null || selectedPhotoPath.isEmpty()) {
            showAlert(AlertType.WARNING, "Photo manquante", 
                     "Aucune photo n'a été sélectionnée", 
                     "Veuillez sélectionner une photo pour l'université.");
            isValid = false;
        }
        
        return isValid;
    }

    @FXML
    private void handleFermerButton() {
        // TODO: Implement logic to close the window or clear the form
    }
    
    /**
     * Cleanup resources when controller is no longer needed
     */
    public void cleanup() {
        if (executorService != null) {
            executorService.shutdown();
        }
    }
} 