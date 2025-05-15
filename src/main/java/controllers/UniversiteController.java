package controllers;

import entities.Universite;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import Services.ServiceUniversite;
import utils.UserSession;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.sql.SQLException;
import java.util.ResourceBundle;
import java.util.UUID;

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
    @FXML
    private Button selectPhotoButton;
    @FXML
    private ImageView photoPreview;
    @FXML
    private Label photoPathLabel;
    
    // Error labels for validation
    @FXML
    private Label nomErrorLabel;
    @FXML
    private Label villeErrorLabel;
    @FXML
    private Label adresseErrorLabel;
    @FXML
    private Label domaineErrorLabel;
    @FXML
    private Label fraisErrorLabel;
    
    private final ServiceUniversite serviceUniversite = new ServiceUniversite();
    private File selectedPhotoFile;
    private final String UPLOAD_DIR = "src/main/resources/images/";
    
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Initialize controller
        // Ensure the upload directory exists
        createUploadDirectory();
        
        // Set default image for preview
        setDefaultImage();
        
        // Setup real-time validation
        setupValidationListeners();
        
        // Set the current user ID in the service
        serviceUniversite.setCurrentUserId(UserSession.getInstance().getUserId());
    }
    
    private void setupValidationListeners() {
        // Add listeners to each field for real-time validation
        nomField.textProperty().addListener((observable, oldValue, newValue) -> validateNomField());
        villeField.textProperty().addListener((observable, oldValue, newValue) -> validateVilleField());
        adresseField.textProperty().addListener((observable, oldValue, newValue) -> validateAdresseField());
        domaineField.textProperty().addListener((observable, oldValue, newValue) -> validateDomaineField());
        fraisField.textProperty().addListener((observable, oldValue, newValue) -> validateFraisField());
    }
    
    private boolean validateNomField() {
        String nom = nomField.getText().trim();
        if (nom.isEmpty()) {
            nomErrorLabel.setText("Le nom est obligatoire");
            nomErrorLabel.setVisible(true);
            return false;
        } else if (!nom.matches("^[\\p{L}\\s'.,-]{3,50}$")) {
            nomErrorLabel.setText("Le nom doit contenir entre 3 et 50 caractères alphabétiques");
            nomErrorLabel.setVisible(true);
            return false;
        } else {
            nomErrorLabel.setVisible(false);
            return true;
        }
    }
    
    private boolean validateVilleField() {
        String ville = villeField.getText().trim();
        if (ville.isEmpty()) {
            villeErrorLabel.setText("La ville est obligatoire");
            villeErrorLabel.setVisible(true);
            return false;
        } else if (!ville.matches("^[\\p{L}\\s-]{2,30}$")) {
            villeErrorLabel.setText("La ville doit contenir entre 2 et 30 caractères alphabétiques");
            villeErrorLabel.setVisible(true);
            return false;
        } else {
            villeErrorLabel.setVisible(false);
            return true;
        }
    }
    
    private boolean validateAdresseField() {
        String adresse = adresseField.getText().trim();
        if (adresse.isEmpty()) {
            adresseErrorLabel.setText("L'adresse est obligatoire");
            adresseErrorLabel.setVisible(true);
            return false;
        } else if (adresse.length() < 5 || adresse.length() > 100) {
            adresseErrorLabel.setText("L'adresse doit contenir entre 5 et 100 caractères");
            adresseErrorLabel.setVisible(true);
            return false;
        } else {
            adresseErrorLabel.setVisible(false);
            return true;
        }
    }
    
    private boolean validateDomaineField() {
        String domaine = domaineField.getText().trim();
        if (domaine.isEmpty()) {
            domaineErrorLabel.setText("Le domaine d'étude est obligatoire");
            domaineErrorLabel.setVisible(true);
            return false;
        } else if (!domaine.matches("^[\\p{L}\\s,'-]{3,50}$")) {
            domaineErrorLabel.setText("Le domaine doit contenir entre 3 et 50 caractères valides");
            domaineErrorLabel.setVisible(true);
            return false;
        } else {
            domaineErrorLabel.setVisible(false);
            return true;
        }
    }
    
    private boolean validateFraisField() {
        String fraisStr = fraisField.getText().trim();
        if (fraisStr.isEmpty()) {
            fraisErrorLabel.setText("Les frais sont obligatoires");
            fraisErrorLabel.setVisible(true);
            return false;
        } else {
            try {
                double frais = Double.parseDouble(fraisStr);
                if (frais < 0) {
                    fraisErrorLabel.setText("Les frais doivent être un nombre positif");
                    fraisErrorLabel.setVisible(true);
                    return false;
                } else {
                    fraisErrorLabel.setVisible(false);
                    return true;
                }
            } catch (NumberFormatException e) {
                fraisErrorLabel.setText("Les frais doivent être un nombre valide");
                fraisErrorLabel.setVisible(true);
                return false;
            }
        }
    }
    
    private void createUploadDirectory() {
        File uploadDir = new File(UPLOAD_DIR);
        if (!uploadDir.exists()) {
            boolean created = uploadDir.mkdirs();
            if (created) {
                System.out.println("Upload directory created: " + UPLOAD_DIR);
            } else {
                System.err.println("Failed to create upload directory: " + UPLOAD_DIR);
            }
        }
    }
    
    private void setDefaultImage() {
        String defaultImagePath = "/images/default_university.png";
        try {
            URL imageUrl = getClass().getResource(defaultImagePath);
            if (imageUrl != null) {
                Image defaultImage = new Image(imageUrl.toExternalForm());
                photoPreview.setImage(defaultImage);
            } else {
                System.err.println("Default image not found: " + defaultImagePath);
            }
        } catch (Exception e) {
            System.err.println("Error loading default image: " + e.getMessage());
        }
    }
    
    @FXML
    private void handleSelectPhotoButton() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Sélectionner une photo");
        
        // Set extension filters
        FileChooser.ExtensionFilter imageFilter = 
            new FileChooser.ExtensionFilter("Images", "*.png", "*.jpg", "*.jpeg", "*.gif");
        fileChooser.getExtensionFilters().add(imageFilter);
        
        // Show open file dialog
        Stage stage = (Stage) selectPhotoButton.getScene().getWindow();
        selectedPhotoFile = fileChooser.showOpenDialog(stage);
        
        if (selectedPhotoFile != null) {
            try {
                // Validate image size (max 5MB)
                long fileSizeInBytes = selectedPhotoFile.length();
                long fileSizeInMB = fileSizeInBytes / (1024 * 1024);
                if (fileSizeInMB > 5) {
                    showAlert(Alert.AlertType.ERROR, "Fichier trop volumineux", 
                              "La taille de l'image ne doit pas dépasser 5MB");
                    return;
                }
                
                // Display the selected image in the preview
                Image image = new Image(selectedPhotoFile.toURI().toString());
                photoPreview.setImage(image);
                
                // Display the file name with the correct path format
                photoPathLabel.setText("images/" + selectedPhotoFile.getName());
            } catch (Exception e) {
                showAlert(Alert.AlertType.ERROR, "Erreur de chargement", 
                          "Impossible de charger l'image: " + e.getMessage());
            }
        }
    }
    
    @FXML
    private void handleAjouterButton() throws SQLException {
        // First validate all fields
        boolean isValid = validateNomField() & 
                         validateVilleField() & 
                         validateAdresseField() & 
                         validateDomaineField() & 
                         validateFraisField();
        
        if (isValid) {
            try {
                String nom = nomField.getText().trim();
                String ville = villeField.getText().trim();
                String adresse = adresseField.getText().trim();
                String domaine = domaineField.getText().trim();
                double frais = Double.parseDouble(fraisField.getText().trim());
                
                // Process the photo if one was selected
                String photoPath = null;
                if (selectedPhotoFile != null) {
                    photoPath = savePhoto(selectedPhotoFile);
                }
                
                // Create Universite object with photo path
                Universite universite = new Universite(nom, ville, adresse, domaine, frais, photoPath);
                
                // Update the service with current user ID before adding
                serviceUniversite.setCurrentUserId(UserSession.getInstance().getUserId());
                
                // Call service to add universite
                serviceUniversite.ajouter(universite);
                
                // Show success message
                showAlert(Alert.AlertType.INFORMATION, "Succès", "Université ajoutée avec succès!");
                
                // Clear the form
                clearFields();
                
            } catch (NumberFormatException e) {
                showAlert(Alert.AlertType.ERROR, "Erreur de format", "Les frais doivent être un nombre valide");
            } catch (IOException e) {
                showAlert(Alert.AlertType.ERROR, "Erreur de fichier", 
                          "Impossible de sauvegarder l'image: " + e.getMessage());
            }
        }
    }
    
    private String savePhoto(File sourceFile) throws IOException {
        // Generate unique file name to avoid conflicts
        String uniqueFileName = UUID.randomUUID().toString() + "_" + sourceFile.getName();
        
        // Create destination path
        Path destinationPath = Paths.get(UPLOAD_DIR, uniqueFileName);
        
        // Copy the file to the destination
        Files.copy(sourceFile.toPath(), destinationPath, StandardCopyOption.REPLACE_EXISTING);
        
        System.out.println("Photo saved to: " + destinationPath);
        
        // Return the relative path to be stored in the database
        return "images/" + uniqueFileName;
    }
    
    @FXML
    private void handleFermerButton() {
        // Close the current window and return to the admin interface
        Stage stage = (Stage) fermerButton.getScene().getWindow();
        stage.close();
    }
    
    private boolean validateFields() {
        boolean isValid = true;
        StringBuilder errorMessages = new StringBuilder("Erreurs de validation:\n");
        
        // Validate Nom field - letters, spaces, and common punctuation only, 3-50 chars
        String nom = nomField.getText().trim();
        if (nom.isEmpty()) {
            errorMessages.append("- Le nom de l'université est obligatoire\n");
            isValid = false;
        } else if (!nom.matches("^[\\p{L}\\s'.,-]{3,50}$")) {
            errorMessages.append("- Le nom doit contenir entre 3 et 50 caractères alphabétiques\n");
            isValid = false;
        }
        
        // Validate Ville field - letters, spaces, and hyphens only, 2-30 chars
        String ville = villeField.getText().trim();
        if (ville.isEmpty()) {
            errorMessages.append("- La ville est obligatoire\n");
            isValid = false;
        } else if (!ville.matches("^[\\p{L}\\s-]{2,30}$")) {
            errorMessages.append("- La ville doit contenir entre 2 et 30 caractères alphabétiques\n");
            isValid = false;
        }
        
        // Validate Adresse field - allow common address characters, 5-100 chars
        String adresse = adresseField.getText().trim();
        if (adresse.isEmpty()) {
            errorMessages.append("- L'adresse est obligatoire\n");
            isValid = false;
        } else if (adresse.length() < 5 || adresse.length() > 100) {
            errorMessages.append("- L'adresse doit contenir entre 5 et 100 caractères\n");
            isValid = false;
        }
        
        // Validate Domaine field - letters, spaces, commas, and hyphens, 3-50 chars
        String domaine = domaineField.getText().trim();
        if (domaine.isEmpty()) {
            errorMessages.append("- Le domaine d'étude est obligatoire\n");
            isValid = false;
        } else if (!domaine.matches("^[\\p{L}\\s,'-]{3,50}$")) {
            errorMessages.append("- Le domaine doit contenir entre 3 et 50 caractères valides\n");
            isValid = false;
        }
        
        // Validate Frais field - must be a positive number
        String fraisStr = fraisField.getText().trim();
        if (fraisStr.isEmpty()) {
            errorMessages.append("- Les frais sont obligatoires\n");
            isValid = false;
        } else {
            try {
                double frais = Double.parseDouble(fraisStr);
                if (frais < 0) {
                    errorMessages.append("- Les frais doivent être un nombre positif\n");
                    isValid = false;
                }
            } catch (NumberFormatException e) {
                errorMessages.append("- Les frais doivent être un nombre valide\n");
                isValid = false;
            }
        }
        
        // Show appropriate error message if validation failed
        if (!isValid) {
            showAlert(Alert.AlertType.ERROR, "Erreur de validation", errorMessages.toString());
        }
        
        return isValid;
    }
    
    private void clearFields() {
        nomField.clear();
        villeField.clear();
        adresseField.clear();
        domaineField.clear();
        fraisField.clear();
        
        // Reset error labels
        nomErrorLabel.setVisible(false);
        villeErrorLabel.setVisible(false);
        adresseErrorLabel.setVisible(false);
        domaineErrorLabel.setVisible(false);
        fraisErrorLabel.setVisible(false);
        
        // Reset photo preview
        setDefaultImage();
        photoPathLabel.setText("");
        selectedPhotoFile = null;
    }
    
    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
} 