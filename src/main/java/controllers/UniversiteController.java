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
    
    private final ServiceUniversite serviceUniversite = new ServiceUniversite();
    private File selectedPhotoFile;
    private final String UPLOAD_DIR = "src/main/resources/uploads/universities/";
    
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Initialize controller
        // Ensure the upload directory exists
        createUploadDirectory();
        
        // Set default image for preview
        setDefaultImage();
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
                // Display the selected image in the preview
                Image image = new Image(selectedPhotoFile.toURI().toString());
                photoPreview.setImage(image);
                
                // Display the file name
                photoPathLabel.setText(selectedPhotoFile.getName());
            } catch (Exception e) {
                showAlert(Alert.AlertType.ERROR, "Erreur de chargement", 
                          "Impossible de charger l'image: " + e.getMessage());
            }
        }
    }
    
    @FXML
    private void handleAjouterButton() throws SQLException {
        if (validateFields()) {
            try {
                String nom = nomField.getText();
                String ville = villeField.getText();
                String adresse = adresseField.getText();
                String domaine = domaineField.getText();
                double frais = Double.parseDouble(fraisField.getText());
                
                // Process the photo if one was selected
                String photoPath = null;
                if (selectedPhotoFile != null) {
                    photoPath = savePhoto(selectedPhotoFile);
                }
                
                // Create Universite object with photo path
                Universite universite = new Universite(nom, ville, adresse, domaine, frais, photoPath);
                
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
        return "uploads/universities/" + uniqueFileName;
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