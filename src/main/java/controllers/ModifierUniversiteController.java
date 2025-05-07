package controllers;

import entities.Universite;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import Services.ServiceUniversite;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

public class ModifierUniversiteController implements Initializable {

    @FXML
    private TextField idField;
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
    private TextField photoPathField;
    @FXML
    private ImageView universiteImageView;
    @FXML
    private Button browseButton;
    @FXML
    private Button modifierButton;
    @FXML
    private Button cancelButton;
    
    private final ServiceUniversite serviceUniversite = new ServiceUniversite();
    private Universite currentUniversite;
    
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Initialize controller
        modifierButton.setDisable(true); // Disable modifier button until a university is loaded
    }
    
    @FXML
    private void handleBrowseButton() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Sélectionner une image");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Images", "*.png", "*.jpg", "*.jpeg", "*.gif")
        );
        
        File selectedFile = fileChooser.showOpenDialog(browseButton.getScene().getWindow());
        if (selectedFile != null) {
            try {
                // Update the path field
                String path = selectedFile.getPath();
                // Convert to relative path if possible
                String resourcePath = "images/" + selectedFile.getName();
                photoPathField.setText(resourcePath);
                
                // Display the image
                Image image = new Image(selectedFile.toURI().toString());
                universiteImageView.setImage(image);
            } catch (Exception e) {
                showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible de charger l'image: " + e.getMessage());
            }
        }
    }
    
    @FXML
    private void handleModifierButton() {
        if (currentUniversite == null) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Aucune université à modifier.");
            return;
        }
        
        if (validateFields()) {
            try {
                // Update current university with form values
                currentUniversite.setNom(nomField.getText());
                currentUniversite.setVille(villeField.getText());
                currentUniversite.setAdresse_universite(adresseField.getText());
                currentUniversite.setDomaine(domaineField.getText());
                currentUniversite.setFrais(Double.parseDouble(fraisField.getText()));
                
                // Update the photo path if changed
                if (!photoPathField.getText().isEmpty()) {
                    currentUniversite.setPhotoPath(photoPathField.getText());
                }
                
                // Call service to modify university
                serviceUniversite.modifier(currentUniversite);
                
                // Show success message
                showAlert(Alert.AlertType.INFORMATION, "Succès", "Université modifiée avec succès!");
                
                // Close the window
                Stage stage = (Stage) modifierButton.getScene().getWindow();
                stage.close();
                
            } catch (NumberFormatException e) {
                showAlert(Alert.AlertType.ERROR, "Erreur de format", "Les frais doivent être un nombre valide");
            } catch (Exception e) {
                showAlert(Alert.AlertType.ERROR, "Erreur", "Une erreur est survenue: " + e.getMessage());
            }
        }
    }
    
    @FXML
    private void handleCancelButton() {
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        stage.close();
    }
    
    private boolean validateFields() {
        if (nomField.getText().isEmpty() || villeField.getText().isEmpty() ||
                adresseField.getText().isEmpty() || domaineField.getText().isEmpty() ||
                fraisField.getText().isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Champs manquants", "Veuillez remplir tous les champs obligatoires");
            return false;
        }
        
        try {
            Double.parseDouble(fraisField.getText());
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Format invalide", "Les frais doivent être un nombre valide");
            return false;
        }
        
        return true;
    }
    
    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public void setUniversite(Universite universite) {
        // Store the university being modified
        currentUniversite = universite;
        
        // Fill the form with university data
        idField.setText(String.valueOf(universite.getId_universite()));
        nomField.setText(universite.getNom());
        villeField.setText(universite.getVille());
        adresseField.setText(universite.getAdresse_universite());
        domaineField.setText(universite.getDomaine());
        fraisField.setText(String.valueOf(universite.getFrais()));
        
        // Set the photo path and load the image
        if (universite.getPhotoPath() != null && !universite.getPhotoPath().isEmpty()) {
            photoPathField.setText(universite.getPhotoPath());
            
            try {
                // Try to load from resources folder
                URL photoUrl = getClass().getResource("/" + universite.getPhotoPath());
                if (photoUrl != null) {
                    Image universityImage = new Image(photoUrl.toExternalForm());
                    universiteImageView.setImage(universityImage);
                } else {
                    // Try as a file path
                    File photoFile = new File("src/main/resources/" + universite.getPhotoPath());
                    if (photoFile.exists()) {
                        Image universityImage = new Image(photoFile.toURI().toString());
                        universiteImageView.setImage(universityImage);
                    } else {
                        // Try direct path
                        try {
                            Image universityImage = new Image(universite.getPhotoPath());
                            universiteImageView.setImage(universityImage);
                        } catch (Exception e) {
                            System.err.println("Could not load image from direct path: " + e.getMessage());
                        }
                    }
                }
            } catch (Exception e) {
                System.err.println("Error loading university photo: " + e.getMessage());
            }
        }
        
        // Enable the modifier button
        modifierButton.setDisable(false);
    }
} 